package com.clou.userdemo.service.impl;

import com.clou.apidemo.POJO.dto.ShopEsDTO;
import com.clou.apidemo.POJO.vo.PageVO;
import com.clou.apidemo.POJO.vo.ShopEsVO;
import com.clou.apidemo.client.ShopClient;
import com.clou.servicecommon.pojo.UserOperationLogPO;
import com.clou.userdemo.POJO.po.ProductDetailsPO;
import com.clou.userdemo.POJO.po.ProductPO;
import com.clou.userdemo.mapper.ProductMapper;
import com.clou.userdemo.mapper.UserOperationLogExtMapper;
import com.clou.userdemo.service.UserRecommendService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRecommendServiceImpl implements UserRecommendService {

    // 操作类型
    private static final int QUERY = 1;
    private static final int ADD_CART = 2;
    private static final int ORDER = 3;
    private static final int CANCEL = 4;
    private static final int PAY = 5;
    private static final int CONFIRM = 6;

    private static final Map<Integer, Double> OPERATE_WEIGHT = new HashMap<>();

    // 操作权重
    static {
        // 查询
        OPERATE_WEIGHT.put(QUERY, 1.0);
        // 添加购物车
        OPERATE_WEIGHT.put(ADD_CART, 3.0);
        // 下单
        OPERATE_WEIGHT.put(ORDER, 6.0);
        // 取消
        OPERATE_WEIGHT.put(CANCEL, -2.0);
        //  支付
        OPERATE_WEIGHT.put(PAY, 10.0);
        // 确认
        OPERATE_WEIGHT.put(CONFIRM, 12.0);
    }

    private static final double RECENT_WEIGHT = 0.6;
    private static final double ALL_WEIGHT = 0.4;
    private static final int RECENT_DAYS = 7;
    private static final String RECOMMEND_CACHE_KEY = "recommend:user:";
    private static final String RECOMMEND_CACHE_KEY_ALL = "recommend:user:all";

    @Autowired
    private UserOperationLogExtMapper userOperationLogExtMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShopClient shopClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取用户推荐
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<ShopEsVO> getRecommendProducts(Long userId, Integer pageNum, Integer pageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }

        if (userId == null) {
            return getRecommendForAll(pageNum, pageSize);
        }

        String cacheKey = RECOMMEND_CACHE_KEY + userId;
        String cacheJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheJson != null) {
            try {
                PageVO<ShopEsVO> cached = objectMapper.readValue(cacheJson, new TypeReference<PageVO<ShopEsVO>>() {});
                log.info("从Redis缓存获取用户{}的推荐数据", userId);
                return paginateFromCache(cached, pageNum, pageSize);
            } catch (Exception e) {
                log.warn("解析推荐缓存失败: {}", e.getMessage());
            }
        }

        Map<Long, Double> productWeightMap = buildProductWeightMap(userId);

        List<Map.Entry<Long, Double>> top5 = productWeightMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
        log.info("用户{}权重map前5: {}", userId, top5);

        List<ShopEsVO> recommendList;
        List<ShopEsVO> fallbackList = null;
        if (productWeightMap.isEmpty()) {
            recommendList = getFallbackProducts();
            log.info("用户{}无行为数据，使用兜底数据，数量={}", userId, recommendList.size());
        } else {
            recommendList = getRecommendByWeight(productWeightMap);
            log.info("用户{}权重推荐数据，数量={}", userId, recommendList.size());
            if (recommendList.size() < pageSize) {
                Set<Long> existIds = recommendList.stream()
                        .map(ShopEsVO::getId)
                        .collect(Collectors.toSet());
                fallbackList = getFallbackProductsExcluding(existIds);
                log.info("用户{}凑数数据，数量={}", userId, fallbackList.size());
                recommendList.addAll(fallbackList);
            }
        }

        long total = recommendList.size();
        PageVO<ShopEsVO> result = new PageVO<>(total, recommendList);

        try {
            String json = objectMapper.writeValueAsString(result);
            stringRedisTemplate.opsForValue().set(cacheKey, json, 30, TimeUnit.MINUTES);
            log.info("用户{}的推荐数据已缓存到Redis", userId);
        } catch (Exception e) {
            log.warn("缓存推荐数据失败: {}", e.getMessage());
        }

        return paginateFromCache(result, pageNum, pageSize);
    }

    /**
     * 获取未登录用户的推荐
     * @param pageNum
     * @param pageSize
     * @return
     */
    private PageVO<ShopEsVO> getRecommendForAll(Integer pageNum, Integer pageSize) {
        String cacheJson = stringRedisTemplate.opsForValue().get(RECOMMEND_CACHE_KEY_ALL);
        if (cacheJson != null) {
            try {
                PageVO<ShopEsVO> cached = objectMapper.readValue(cacheJson, new TypeReference<PageVO<ShopEsVO>>() {});
                log.info("从Redis缓存获取未登录用户的推荐数据");
                return paginateFromCache(cached, pageNum, pageSize);
            } catch (Exception e) {
                log.warn("解析未登录推荐缓存失败: {}", e.getMessage());
            }
        }

        List<ShopEsVO> recommendList = getFallbackProducts();
        log.info("未登录用户使用兜底数据，数量={}", recommendList.size());

        long total = recommendList.size();
        PageVO<ShopEsVO> result = new PageVO<>(total, recommendList);

        try {
            String json = objectMapper.writeValueAsString(result);
            stringRedisTemplate.opsForValue().set(RECOMMEND_CACHE_KEY_ALL, json, 30, TimeUnit.MINUTES);
            log.info("未登录用户推荐数据已缓存到Redis");
        } catch (Exception e) {
            log.warn("缓存未登录推荐数据失败: {}", e.getMessage());
        }

        return paginateFromCache(result, pageNum, pageSize);
    }

    /**
     * 构建用户行为权重
     * @param userId
     * @return
     */
    private Map<Long, Double> buildProductWeightMap(Long userId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(RECENT_DAYS);

        List<UserOperationLogPO> recentLogs = userOperationLogExtMapper.getByUserIdAndTimeAfter(userId, sevenDaysAgo);
        List<UserOperationLogPO> allLogs = userOperationLogExtMapper.getByUserId(userId);

        Map<Long, Double> recentWeightMap = calculateWeightFromLogs(recentLogs);
        Map<Long, Double> allWeightMap = calculateWeightFromLogs(allLogs);

        Map<Long, Double> combinedMap = new HashMap<>();

        for (Map.Entry<Long, Double> entry : allWeightMap.entrySet()) {
            combinedMap.put(entry.getKey(), entry.getValue() * ALL_WEIGHT);
        }

        for (Map.Entry<Long, Double> entry : recentWeightMap.entrySet()) {
            combinedMap.merge(entry.getKey(), entry.getValue() * RECENT_WEIGHT, Double::sum);
        }

        return combinedMap;
    }

    /**
     * 计算用户行为权重
     * @param logs
     * @return
     */
    private Map<Long, Double> calculateWeightFromLogs(List<UserOperationLogPO> logs) {
        Map<Long, Double> weightMap = new HashMap<>();

        for (UserOperationLogPO logPO : logs) {
            int operateType = logPO.getOperateType();
            Double weight = OPERATE_WEIGHT.getOrDefault(operateType, 0.0);

            List<Long> productIds = parseProductIds(logPO.getOperateData());
            for (Long productId : productIds) {
                weightMap.merge(productId, weight, Double::sum);
            }
        }

        return weightMap;
    }

    /**
     * 解析操作数据
     * @param operateData
     * @return
     */
    private List<Long> parseProductIds(String operateData) {
        if (operateData == null || operateData.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(operateData, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.warn("解析operate_data失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 获取推荐商品
     * @param productWeightMap
     * @return
     */
    private List<ShopEsVO> getRecommendByWeight(Map<Long, Double> productWeightMap) {
        List<Long> sortedProductIds = productWeightMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Set<String> userLabels = collectLabelsFromProducts(sortedProductIds);

        if (userLabels.isEmpty()) {
            return convertToShopEsVO(sortedProductIds);
        }

        List<ProductDetailsPO> allDetails = productMapper.getAllProductDetails();
        Map<Long, Double> recommendScoreMap = new HashMap<>();

        for (ProductDetailsPO detail : allDetails) {
            if (detail.getLabel() == null || detail.getLabel().isEmpty()) {
                continue;
            }
            Set<String> productLabels = parseLabels(detail.getLabel());
            long matchCount = productLabels.stream()
                    .filter(userLabels::contains)
                    .count();
            if (matchCount > 0) {
                recommendScoreMap.merge(detail.getProductId(), (double) matchCount, Double::sum);
            }
        }

        for (Long pid : sortedProductIds) {
            if (!recommendScoreMap.containsKey(pid)) {
                Double originalWeight = productWeightMap.getOrDefault(pid, 0.0);
                recommendScoreMap.put(pid, originalWeight + 100.0);
            } else {
                recommendScoreMap.merge(pid, 100.0, Double::sum);
            }
        }

        List<Long> recommendIds = recommendScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return convertToShopEsVO(recommendIds);
    }

    /**
     * 获取用户标签
     * @param productIds
     * @return
     */
    private Set<String> collectLabelsFromProducts(List<Long> productIds) {
        Set<String> labels = new HashSet<>();
        for (Long productId : productIds) {
            ProductDetailsPO detail = productMapper.getProductDetailsByProductId(productId);
            if (detail != null && detail.getLabel() != null && !detail.getLabel().isEmpty()) {
                labels.addAll(parseLabels(detail.getLabel()));
            }
        }
        return labels;
    }

    /**
     * 解析标签
     * @param labelJson
     * @return
     */
    private Set<String> parseLabels(String labelJson) {
        try {
            List<String> labelList = objectMapper.readValue(labelJson, new TypeReference<List<String>>() {});
            return new HashSet<>(labelList);
        } catch (Exception e) {
            log.debug("解析label失败: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * 转换为ShopEsVO
     * @param productIds
     * @return
     */
    private List<ShopEsVO> convertToShopEsVO(List<Long> productIds) {
        List<ShopEsVO> result = new ArrayList<>();
        for (Long productId : productIds) {
            ProductPO product = productMapper.getProductById(productId);
            if (product == null) {
                continue;
            }
            ProductDetailsPO detail = productMapper.getProductDetailsByProductId(productId);
            ShopEsVO vo = new ShopEsVO();
            vo.setId(product.getId());
            vo.setName(product.getName());
            vo.setPrice(product.getPrice());
            vo.setCategory(product.getCategory());
            vo.setStock(product.getStock());
            vo.setMerchantId(product.getMerchantId());
            if (detail != null) {
                vo.setImageUrls(detail.getImageUrls());
            }
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取回退商品
     * @return
     */
    private List<ShopEsVO> getFallbackProducts() {
        ShopEsDTO shopEsDTO = new ShopEsDTO();
        shopEsDTO.setPageNum(1);
        PageVO<ShopEsVO> pageVO = shopClient.searchShop(shopEsDTO);
        return pageVO != null && pageVO.getData() != null ? pageVO.getData() : Collections.emptyList();
    }

    /**
     * 获取回退商品（排除指定ID）
     * @param excludeIds
     * @return
     */
    private List<ShopEsVO> getFallbackProductsExcluding(Set<Long> excludeIds) {
        List<ShopEsVO> fallback = getFallbackProducts();
        return fallback.stream()
                .filter(vo -> !excludeIds.contains(vo.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 分页从缓存中获取数据
     * @param allData
     * @param pageNum
     * @param pageSize
     * @return
     */
    private PageVO<ShopEsVO> paginateFromCache(PageVO<ShopEsVO> allData, Integer pageNum, Integer pageSize) {
        List<ShopEsVO> allList = allData.getData();
        if (allList == null) {
            return allData;
        }
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= allList.size()) {
            return new PageVO<>(allData.getTotal(), Collections.emptyList());
        }
        int toIndex = Math.min(fromIndex + pageSize, allList.size());
        return new PageVO<>(allData.getTotal(), allList.subList(fromIndex, toIndex));
    }

    /**
     * 刷新所有用户的推荐缓存
     */
    @Override
    public void refreshAllRecommendCache() {
        Set<String> oldKeys = stringRedisTemplate.keys(RECOMMEND_CACHE_KEY + "*");
        if (oldKeys != null && !oldKeys.isEmpty()) {
            stringRedisTemplate.delete(oldKeys);
            log.info("已删除{}条旧推荐缓存", oldKeys.size());
        }

        List<Long> userIds = userOperationLogExtMapper.getAllUserIds();
        log.info("开始重新计算推荐缓存，共{}个用户", userIds.size());

        int successCount = 0;
        for (Long userId : userIds) {
            try {
                Map<Long, Double> productWeightMap = buildProductWeightMap(userId);

                List<ShopEsVO> recommendList;
                if (productWeightMap.isEmpty()) {
                    recommendList = getFallbackProducts();
                } else {
                    recommendList = getRecommendByWeight(productWeightMap);
                    if (recommendList.size() < 20) {
                        Set<Long> existIds = recommendList.stream()
                                .map(ShopEsVO::getId)
                                .collect(Collectors.toSet());
                        List<ShopEsVO> fallback = getFallbackProductsExcluding(existIds);
                        recommendList.addAll(fallback);
                    }
                }

                long total = recommendList.size();
                PageVO<ShopEsVO> result = new PageVO<>(total, recommendList);

                String cacheKey = RECOMMEND_CACHE_KEY + userId;
                String json = objectMapper.writeValueAsString(result);
                stringRedisTemplate.opsForValue().set(cacheKey, json, 30, TimeUnit.MINUTES);
                successCount++;
            } catch (Exception e) {
                log.error("重新计算用户{}推荐缓存失败", userId, e);
            }
        }

        log.info("推荐缓存重新计算完成，成功{}个，共{}个用户", successCount, userIds.size());
    }

}
