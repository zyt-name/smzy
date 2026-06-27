package com.clou.shopdemo.service.impl;

import com.clou.common.exception.BusinessException;
import com.clou.shopdemo.POJO.po.ShopDetailsPO;
import com.clou.shopdemo.POJO.po.ShopEsPO;
import com.clou.shopdemo.POJO.po.ShopPO;
import com.clou.shopdemo.mapper.ShopMapper;
import com.clou.shopdemo.service.ShopSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShopSyncServiceImpl implements ShopSyncService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private RestHighLevelClient client;

    private static final String INDEX_NAME = "shop";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 同步所有数据到 ES
     * @throws IOException
     */
    @Override
    @CacheEvict(cacheNames = "searchShop", allEntries = true)
    public void syncAllToEs() throws IOException {
        try {
            // 1. 删除旧索引（如果存在）
            GetIndexRequest getRequest = new GetIndexRequest(INDEX_NAME);
            if (client.indices().exists(getRequest, RequestOptions.DEFAULT)) {
                DeleteIndexRequest deleteRequest = new DeleteIndexRequest(INDEX_NAME);
                client.indices().delete(deleteRequest, RequestOptions.DEFAULT);
            }

            // 2. 重新创建索引，配置 IK 分词器 mapping
            CreateIndexRequest createRequest = new CreateIndexRequest(INDEX_NAME);
            String mapping = "{\n" +
                    "  \"properties\": {\n" +
                    "    \"name\": {\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"ik_max_word\",\n" +
                    "      \"search_analyzer\": \"ik_smart\"\n" +
                    "    },\n" +
                    "    \"category\": {\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"ik_max_word\",\n" +
                    "      \"search_analyzer\": \"ik_smart\"\n" +
                    "    },\n" +
                    "    \"price\": {\n" +
                    "      \"type\": \"float\"\n" +
                    "    },\n" +
                    "    \"stock\": {\n" +
                    "      \"type\": \"long\"\n" +
                    "    },\n" +
                    "    \"merchantId\": {\n" +
                    "      \"type\": \"long\"\n" +
                    "    },\n" +
                    "    \"status\": {\n" +
                    "      \"type\": \"integer\"\n" +
                    "    },\n" +
                    "    \"adminStatus\": {\n" +
                    "      \"type\": \"integer\"\n" +
                    "    },\n" +
                    "    \"imageUrls\": {\n" +
                    "      \"type\": \"text\"\n" +
                    "    },\n" +
                    "    \"label\": {\n" +
                    "      \"type\": \"text\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            createRequest.mapping(mapping, XContentType.JSON);
            client.indices().create(createRequest, RequestOptions.DEFAULT);

            // 3. 从数据库查询所有数据
            List<ShopPO> shops = shopMapper.getAllShops();
            log.info("主表：{}", shops);
            List<ShopDetailsPO> details = shopMapper.getAllShopDetails();
            log.info("详情表：{}", details);
            
            if (shops == null || shops.isEmpty()) {
                return;
            }

            // 将详情转为 Map 方便匹配
            Map<Long, ShopDetailsPO> detailsMap = details.stream()
                    .collect(Collectors.toMap(ShopDetailsPO::getShopId, d -> d, (k1, k2) -> k1));

            // 4. 批量导入到 ES
            BulkRequest bulkRequest = new BulkRequest();
            for (ShopPO shop : shops) {
                ShopEsPO shopEsPO = new ShopEsPO();
                BeanUtils.copyProperties(shop, shopEsPO);
                
                // 补充详情信息
                ShopDetailsPO detail = detailsMap.get(shop.getId());
                if (detail != null) {
                    BeanUtils.copyProperties(detail, shopEsPO);
                }

                String json = objectMapper.writeValueAsString(shopEsPO);
                IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                        .id(String.valueOf(shop.getId()))
                        .source(json, XContentType.JSON);
                bulkRequest.add(indexRequest);
            }

            if (bulkRequest.numberOfActions() > 0) {
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
            }

        } catch (IOException e) {
            throw new BusinessException(0, "同步 ES 数据失败: " + e.getMessage());
        }
    }

    /**
     * 按商家ID同步商品到ES
     * @param merchantId
     * @throws IOException
     */
    @Override
    public void syncByMerchantId(Long merchantId) throws IOException {
        try {
            // 1. 从数据库查询该商家的所有商品
            List<ShopPO> shops = shopMapper.getShopsByMerchantId(merchantId);
            if (shops == null || shops.isEmpty()) {
                log.info("商家{}没有商品需要同步", merchantId);
                return;
            }

            // 获取所有商品的详情
            List<Long> shopIds = shops.stream()
                    .map(ShopPO::getId)
                    .collect(Collectors.toList());
            List<ShopDetailsPO> details = shopMapper.getShopDetailsByShopIds(shopIds);
            Map<Long, ShopDetailsPO> detailsMap = details.stream()
                    .collect(Collectors.toMap(ShopDetailsPO::getShopId, d -> d, (k1, k2) -> k1));

            // 2. 批量更新ES
            BulkRequest bulkRequest = new BulkRequest();
            for (ShopPO shop : shops) {
                ShopEsPO shopEsPO = new ShopEsPO();
                BeanUtils.copyProperties(shop, shopEsPO);

                // 补充详情信息
                ShopDetailsPO detail = detailsMap.get(shop.getId());
                if (detail != null) {
                    BeanUtils.copyProperties(detail, shopEsPO);
                }

                String json = objectMapper.writeValueAsString(shopEsPO);
                IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                        .id(String.valueOf(shop.getId()))
                        .source(json, XContentType.JSON);
                bulkRequest.add(indexRequest);
            }

            if (bulkRequest.numberOfActions() > 0) {
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
                log.info("商家{}的商品已同步到ES，共{}条", merchantId, bulkRequest.numberOfActions());
            }

        } catch (IOException e) {
            throw new BusinessException(0, "同步商家商品到ES失败: " + e.getMessage());
        }
    }

    /**
     * 按商品ID同步单个商品到ES
     * @param shopId 商品ID
     * @throws IOException
     */
    @Override
    public void syncByShopId(Long shopId) throws IOException {
        syncByShopIds(Collections.singletonList(shopId));
    }

    /**
     * 批量按商品ID同步商品到ES
     * @param shopIds 商品ID列表
     * @throws IOException
     */
    @Override
    public void syncByShopIds(List<Long> shopIds) throws IOException {
        if (shopIds == null || shopIds.isEmpty()) {
            return;
        }

        try {
            BulkRequest bulkRequest = new BulkRequest();
            
            for (Long shopId : shopIds) {
                ShopPO shop = shopMapper.getShopById(shopId.intValue());
                if (shop == null) {
                    log.warn("商品{}不存在，跳过同步", shopId);
                    continue;
                }

                ShopDetailsPO detail = shopMapper.getShopDetailsById(shopId.intValue());
                
                ShopEsPO shopEsPO = new ShopEsPO();
                BeanUtils.copyProperties(shop, shopEsPO);
                if (detail != null) {
                    BeanUtils.copyProperties(detail, shopEsPO);
                }

                String json = objectMapper.writeValueAsString(shopEsPO);
                IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                        .id(String.valueOf(shopId))
                        .source(json, XContentType.JSON);
                bulkRequest.add(indexRequest);
            }

            if (bulkRequest.numberOfActions() > 0) {
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
                log.info("商品已同步到ES，共{}条", bulkRequest.numberOfActions());
            }

        } catch (IOException e) {
            throw new BusinessException(0, "同步商品到ES失败: " + e.getMessage());
        }
    }

    /**
     * 从ES中删除商品
     * @param shopId 商品ID
     * @throws IOException
     */
    @Override
    public void deleteFromEs(Long shopId) throws IOException {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, String.valueOf(shopId));
            client.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("商品{}已从ES中删除", shopId);
        } catch (IOException e) {
            throw new BusinessException(0, "从ES删除商品失败: " + e.getMessage());
        }
    }
}
