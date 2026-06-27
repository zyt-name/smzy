package com.clou.shopdemo.service;

import java.io.IOException;
import java.util.List;

public interface ShopSyncService {
    /**
     * 全量同步 ES 数据：删除旧数据并从数据库重新导入
     */
    void syncAllToEs() throws IOException;

    /**
     * 按商家ID同步商品到ES
     * @param merchantId
     * @throws IOException
     */
    void syncByMerchantId(Long merchantId) throws IOException;

    /**
     * 按商品ID同步单个商品到ES
     * @param shopId 商品ID
     * @throws IOException
     */
    void syncByShopId(Long shopId) throws IOException;

    /**
     * 批量按商品ID同步商品到ES
     * @param shopIds 商品ID列表
     * @throws IOException
     */
    void syncByShopIds(List<Long> shopIds) throws IOException;

    /**
     * 从ES中删除商品
     * @param shopId 商品ID
     * @throws IOException
     */
    void deleteFromEs(Long shopId) throws IOException;
}
