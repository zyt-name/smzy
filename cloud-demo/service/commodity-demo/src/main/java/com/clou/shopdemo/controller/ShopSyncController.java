package com.clou.shopdemo.controller;

import com.clou.common.result.Result;
import com.clou.shopdemo.service.ShopSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/shop/sync")
public class ShopSyncController {

    @Autowired
    private ShopSyncService shopSyncService;

    /**
     * 全量同步ES
     * @return
     * @throws IOException
     */
    @PostMapping("/all")
    public Result<String> syncAll() throws IOException {
        shopSyncService.syncAllToEs();
        return Result.success("ES数据全量同步成功");
    }

    /**
     * 按商家ID同步商品到ES
     * @param merchantId
     * @return
     * @throws IOException
     */
    @PostMapping("/byMerchant")
    public Result<String> syncByMerchant(@RequestParam("merchantId") Long merchantId) throws IOException {
        shopSyncService.syncByMerchantId(merchantId);
        return Result.success("商家商品同步到ES成功");
    }

    /**
     * 按商品ID同步单个商品到ES
     * @param shopId 商品ID
     * @return
     * @throws IOException
     */
    @PostMapping("/byShopId")
    public Result<String> syncByShopId(@RequestParam("shopId") Long shopId) throws IOException {
        shopSyncService.syncByShopId(shopId);
        return Result.success("商品同步到ES成功");
    }

    /**
     * 批量按商品ID同步商品到ES
     * @param shopIds 商品ID列表
     * @return
     * @throws IOException
     */
    @PostMapping("/byShopIds")
    public Result<String> syncByShopIds(@RequestParam("shopIds") List<Long> shopIds) throws IOException {
        shopSyncService.syncByShopIds(shopIds);
        return Result.success("商品批量同步到ES成功");
    }

    /**
     * 从ES中删除商品
     * @param shopId 商品ID
     * @return
     * @throws IOException
     */
    @DeleteMapping("/byShopId")
    public Result<String> deleteFromEs(@RequestParam("shopId") Long shopId) throws IOException {
        shopSyncService.deleteFromEs(shopId);
        return Result.success("商品从ES删除成功");
    }
}
