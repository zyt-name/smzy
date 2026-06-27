package com.clou.shopdemo.service.impl;

import com.clou.common.exception.BusinessException;
import com.clou.shopdemo.POJO.dto.AdminShopSearchDTO;
import com.clou.shopdemo.POJO.dto.ShopDTO;
import com.clou.shopdemo.POJO.dto.ShopEsDTO;
import com.clou.shopdemo.POJO.dto.ShopUpdateDTO;
import com.clou.shopdemo.POJO.po.ShopDetailsPO;
import com.clou.shopdemo.POJO.po.ShopEsPO;
import com.clou.shopdemo.POJO.po.ShopPO;
import com.clou.shopdemo.POJO.vo.PageVO;
import com.clou.shopdemo.POJO.vo.ShopDetailsVO;
import com.clou.shopdemo.POJO.vo.ShopEsVO;
import com.clou.shopdemo.mapper.ShopMapper;
import com.clou.shopdemo.service.ShopService;
import com.clou.shopdemo.service.ShopSyncService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    ShopMapper shopMapper;
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ShopSyncService shopSyncService;


    /**
     * 添加商品
     * @param shopDTO
     * @param merchantId
     * @throws IOException
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "searchShop", allEntries = true)
    public void addShop(ShopDTO shopDTO,Long merchantId) throws IOException {
        // 添加商品表部分
        ShopPO shopPO = new ShopPO();
        BeanUtils.copyProperties(shopDTO, shopPO);
        shopPO.setMerchantId(merchantId);
        shopPO.setStatus(0);
        shopPO.setCreatedAt(LocalDateTime.now());
        shopPO.setAdminStatus(0);
        shopMapper.addShop(shopPO);
        //商品id回填得到商品id
        Long shopId = shopPO.getId();

        // 添加商品详情部分
        ShopDetailsPO shopDetailsPO = new ShopDetailsPO();
        BeanUtils.copyProperties(shopDTO, shopDetailsPO);
        shopDetailsPO.setShopId(shopId);
        shopDetailsPO.setUpdatedAt(LocalDateTime.now());
        shopMapper.addShopDetail(shopDetailsPO);

        // 同步到ES
        shopSyncService.syncByShopId(shopId);
    }

    /**
     * 更新商品
     * @param shopUpdateDTO
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "searchShop", allEntries = true)
    public void updateShop(ShopUpdateDTO shopUpdateDTO) throws IOException {
        // 更新商品表部分
        shopMapper.updateShop(shopUpdateDTO);
        // 将空字符串转为 null，避免 MyBatis 动态 SQL 更新空值
        if (shopUpdateDTO.getDescription() != null && shopUpdateDTO.getDescription().isEmpty()) {
            shopUpdateDTO.setDescription(null);
        }
        if (shopUpdateDTO.getSpecification() != null && shopUpdateDTO.getSpecification().isEmpty()) {
            shopUpdateDTO.setSpecification(null);
        }
        if (shopUpdateDTO.getImageUrls() != null && shopUpdateDTO.getImageUrls().isEmpty()) {
            shopUpdateDTO.setImageUrls(null);
        }
        if (shopUpdateDTO.getLabel() != null && shopUpdateDTO.getLabel().isEmpty()) {
            shopUpdateDTO.setLabel(null);
        }
        // 更新商品详情部分（只有当有字段需要更新时才执行）
        if (shopUpdateDTO.getDescription() != null ||
            shopUpdateDTO.getSpecification() != null ||
            shopUpdateDTO.getImageUrls() != null ||
            shopUpdateDTO.getLabel() != null) {
            shopMapper.updateShopDetail(shopUpdateDTO);
        }
        // 同步到ES
        shopSyncService.syncByShopId(shopUpdateDTO.getId());
    }

    /**
     * 上/下架商品
     * @param shopUpdateDTO
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "searchShop", allEntries = true)
    public void updateShopStatus(ShopUpdateDTO shopUpdateDTO) throws IOException {
        // 更新商品状态(只需要更新商品表的状态即可)
        shopMapper.updateShop(shopUpdateDTO);
        // 将空字符串转为 null，避免 MyBatis 动态 SQL 更新空值
        if (shopUpdateDTO.getImageUrls() != null && shopUpdateDTO.getImageUrls().isEmpty()) {
            shopUpdateDTO.setImageUrls(null);
        }
        if (shopUpdateDTO.getLabel() != null && shopUpdateDTO.getLabel().isEmpty()) {
            shopUpdateDTO.setLabel(null);
        }
        // 如果传了图片或标签，则更新商品详情表
        if (shopUpdateDTO.getImageUrls() != null || shopUpdateDTO.getLabel() != null) {
            shopMapper.updateShopDetail(shopUpdateDTO);
        }
        // 同步到ES
        shopSyncService.syncByShopId(shopUpdateDTO.getId());
    }

    /**
     * 管理员更新商品admin_status状态（强制下架/恢复正常）
     * @param shopId 商品ID
     * @param adminStatus 管理员状态（0-正常，1-强制下架）
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "searchShop", allEntries = true)
    public void updateShopAdminStatus(Long shopId, Integer adminStatus) throws IOException {
        // 更新商品admin_status状态
        shopMapper.updateShopAdminStatus(shopId, adminStatus);
        // 同步到ES
        shopSyncService.syncByShopId(shopId);
    }

    /**
     * 删除商品
     * @param shopId
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "searchShop", allEntries = true)
    public void deleteShop(Long shopId) throws IOException {
        // 删除商品表
        shopMapper.deleteShop(shopId);
        // 删除商品详情表
        shopMapper.deleteShopDetail(shopId);
        // 从ES中删除
        shopSyncService.deleteFromEs(shopId);
    }

    /**
     * 商家/管理员根据商户id查询商品
     * @param merchantId
     * @return
     */
    @Override
    public List<ShopPO> byMerchantId(Long merchantId) {
        List<ShopPO> list= shopMapper.byMerchantId(merchantId);
        return list;
    }

    /**
     * 查询商品 - 分页查询
     * @param shopEsDTO
     * @return
     * @throws IOException
     */
    @Override
    @Cacheable(cacheNames = "searchShop",
            key = "'shop_' + " +
                    "(#shopEsDTO.name ?: 'null') + '_' + " +
                    "(#shopEsDTO.categories != null ? #shopEsDTO.categories.toString() : 'null') + '_' + " +
                    "(#shopEsDTO.maxPrice ?: 'null') + '_' + " +
                    "(#shopEsDTO.minPrice ?: 'null') + '_' + " +
                    "(#shopEsDTO.merchantId ?: 'null') + '_' + " +
                    "(#shopEsDTO.pageNum ?: 1)")
    public PageVO<ShopEsVO> searchShop(ShopEsDTO shopEsDTO) throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 商家ID精确匹配
        if (shopEsDTO.getMerchantId() != null) {
            boolQuery.filter(QueryBuilders.termQuery("merchantId", shopEsDTO.getMerchantId()));
        }

        // 分类模糊匹配（支持多个分类，使用OR查询，使用IK分词器）
        if (shopEsDTO.getCategories() != null && !shopEsDTO.getCategories().isEmpty()) {
            BoolQueryBuilder categoryBoolQuery = QueryBuilders.boolQuery();
            for (String category : shopEsDTO.getCategories()) {
                if (category != null && !category.isEmpty()) {
                    MatchQueryBuilder categoryMatch = QueryBuilders.matchQuery("category", category);
                    categoryMatch.analyzer("ik_smart");
                    categoryBoolQuery.should(categoryMatch);
                }
            }
            boolQuery.filter(categoryBoolQuery);
        }

        // 名称搜索（模糊匹配，使用IK分词器）
        if (shopEsDTO.getName() != null && !shopEsDTO.getName().isEmpty()) {
            MatchQueryBuilder nameMatch = QueryBuilders.matchQuery("name", shopEsDTO.getName());
            nameMatch.analyzer("ik_smart");
            boolQuery.must(nameMatch);
        }

        // 价格范围查询（排除0.00默认值的影响）
        Float minPrice = shopEsDTO.getMinPrice();
        Float maxPrice = shopEsDTO.getMaxPrice();
        // 标记是否需要添加价格范围查询
        boolean needPriceQuery = false;
        RangeQueryBuilder priceRange = QueryBuilders.rangeQuery("price");
        // 处理最小价格：不为null且不等于0.00时添加条件
        if (minPrice != null && minPrice.compareTo(0.00f) != 0) {
            priceRange.gte(minPrice);
            needPriceQuery = true;
        }
        // 处理最大价格：不为null且不等于0.00时添加条件
        if (maxPrice != null && maxPrice.compareTo(0.00f) != 0) {
            priceRange.lte(maxPrice);
            needPriceQuery = true;
        }
        // 只有当需要时才添加价格范围查询
        if (needPriceQuery) {
            boolQuery.must(priceRange);
        }


        // 状态筛选（必选：只查上架商品）
        boolQuery.filter(QueryBuilders.termQuery("status", 0));

        // 管理员状态筛选（必选：只查admin_status为0的正常商品）
        boolQuery.filter(QueryBuilders.termQuery("adminStatus", 0));

        // 获取分页参数，默认第1页
        int pageNum = shopEsDTO.getPageNum() != null ? shopEsDTO.getPageNum() : 1;
        int pageSize = ShopEsDTO.PAGE_SIZE;
        int from = (pageNum - 1) * pageSize;

        //组织搜索请求，并默认按照价格升序排序
        SearchRequest request = new SearchRequest("shop");
        request.source().query(boolQuery).sort("price", SortOrder.ASC);
        // 设置分页参数
        request.source().from(from).size(pageSize);

        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 获取总条数
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;

        // 解析结果
        List<ShopEsVO> list = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            // 解析_source获取原始数据
            Map<String, Object> sourceMap = hit.getSourceAsMap();

            ShopEsVO shopEsVO = new ShopEsVO();
            // 设置文档ID
            shopEsVO.setId(Long.valueOf(hit.getId()));

            // 从source中获取字段值并设置到实体
            shopEsVO.setCategory((String) sourceMap.get("category"));
            shopEsVO.setName((String) sourceMap.get("name"));
            shopEsVO.setImageUrls((String) sourceMap.get("imageUrls"));

            // 1. 先把 Object 转成 Number（Integer 和 Long 的共同父类，肯定能转成）
            Number stockNum = (Number) sourceMap.get("stock");
            // 2. 再统一转成 Long（不管原先是 Integer 还是 Long，都能拿到 long 值）
            shopEsVO.setStock(stockNum != null ? stockNum.longValue() : null);

            // 处理商家id字段
            Number merchantIdNum = (Number) sourceMap.get("merchantId");
            shopEsVO.setMerchantId(merchantIdNum != null ? merchantIdNum.longValue() : null);

            // 处理价格字段（避免空指针和类型转换异常）
            if (sourceMap.get("price") != null) {
                shopEsVO.setPrice(Float.parseFloat(sourceMap.get("price").toString()));
            }

            // 处理标签字段
            if (sourceMap.get("label") != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    shopEsVO.setLabel(objectMapper.readValue(sourceMap.get("label").toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
                } catch (Exception e) {
                    shopEsVO.setLabel(null);
                }
            }

            // 处理状态字段
            if (sourceMap.get("status") != null) {
                shopEsVO.setStatus(((Number) sourceMap.get("status")).intValue());
            }

            // 处理管理员状态字段
            if (sourceMap.get("adminStatus") != null) {
                shopEsVO.setAdminStatus(((Number) sourceMap.get("adminStatus")).intValue());
            }

            list.add(shopEsVO);
        }

        // 封装分页结果
        PageVO<ShopEsVO> pageVO = new PageVO<>();
        pageVO.setTotal(total);
        pageVO.setData(list);
        return pageVO;
    }

    /**
     * 管理员查询商品 - 分页查询（直接查询数据库，不过滤状态，查看所有商品）
     * @param adminShopSearchDTO
     * @return
     */
    @Override
    public PageVO<ShopEsVO> adminSearchShop(AdminShopSearchDTO adminShopSearchDTO) {
        // 获取分页参数
        int pageNum = adminShopSearchDTO.getPageNum() != null ? adminShopSearchDTO.getPageNum() : 1;
        int pageSize = adminShopSearchDTO.getPageSize() != null ? adminShopSearchDTO.getPageSize() : 12;
        int offset = (pageNum - 1) * pageSize;

        // 查询商品列表
        List<ShopPO> shopList = shopMapper.adminSearchShops(
                adminShopSearchDTO.getName(),
                adminShopSearchDTO.getMerchantId(),
                adminShopSearchDTO.getCategory(),
                offset,
                pageSize
        );

        // 查询总数
        long total = shopMapper.adminSearchShopsCount(
                adminShopSearchDTO.getName(),
                adminShopSearchDTO.getMerchantId(),
                adminShopSearchDTO.getCategory()
        );

        // 转换为 ShopEsVO
        List<ShopEsVO> list = shopList.stream().map(shop -> {
            ShopEsVO vo = new ShopEsVO();
            vo.setId(shop.getId());
            vo.setName(shop.getName());
            vo.setPrice(shop.getPrice());
            vo.setCategory(shop.getCategory());
            vo.setImageUrls(shop.getImageUrls());
            vo.setStock(shop.getStock());
            vo.setMerchantId(shop.getMerchantId());
            vo.setStatus(shop.getStatus());
            vo.setAdminStatus(shop.getAdminStatus());
            return vo;
        }).collect(Collectors.toList());

        // 封装分页结果
        PageVO<ShopEsVO> pageVO = new PageVO<>();
        pageVO.setTotal(total);
        pageVO.setData(list);
        return pageVO;
    }

    /**
     * 根据商品id查询商品详情
     * @param shopId
     * @return
     */
    @Override
    public ShopDetailsVO byShopIdDetails(Long shopId) {
        // 查询商品详情
        ShopDetailsPO  shopDetailsPO=shopMapper.getShopDetailsById(Math.toIntExact(shopId));
        // 查询商品
        ShopPO shopPO=shopMapper.getShopById(Math.toIntExact(shopId));
        // 封装所有的查询结果
        ShopDetailsVO shopDetailsVO=new ShopDetailsVO();
        BeanUtils.copyProperties(shopDetailsPO, shopDetailsVO);

        // 处理label字段的JSON转换
        if (shopDetailsPO.getLabel() != null && !shopDetailsPO.getLabel().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                shopDetailsVO.setLabel(objectMapper.readValue(shopDetailsPO.getLabel(), objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
            } catch (Exception e) {
                shopDetailsVO.setLabel(null);
            }
        }

        shopDetailsVO.setName(shopPO.getName());
        shopDetailsVO.setShopId(shopPO.getId());
        shopDetailsVO.setCategory(shopPO.getCategory());
        shopDetailsVO.setPrice(shopPO.getPrice());
        shopDetailsVO.setMerchantId(shopPO.getMerchantId());
        return shopDetailsVO;
    }

    @Override
    public Map<Long, String> getImagesByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }
        List<ShopDetailsPO> detailsList = shopMapper.getImagesByProductIds(productIds);
        return detailsList.stream()
                .collect(Collectors.toMap(
                        ShopDetailsPO::getShopId,
                        ShopDetailsPO::getImageUrls,
                        (existing, replacement) -> existing
                ));
    }

}
