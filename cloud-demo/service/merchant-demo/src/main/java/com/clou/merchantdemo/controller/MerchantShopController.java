package com.clou.merchantdemo.controller;

import com.clou.apidemo.POJO.dto.ShopDTO;
import com.clou.apidemo.POJO.dto.ShopUpdateDTO;
import com.clou.apidemo.POJO.po.ShopPO;
import com.clou.apidemo.POJO.vo.ShopDetailsVO;
import com.clou.apidemo.client.ShopClient;
import com.clou.common.result.Result;
import com.clou.merchantdemo.POJO.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LENOVO
 */
@RestController
@Slf4j
@RequestMapping("/merchant/shop")
public class MerchantShopController {

    @Autowired
    ShopClient shopClient;

    /**
     * 商家添加商品
     * @param shopDTO
     * @param id
     * @return
     */
    @PostMapping("/add")
    public Result addShop(@RequestBody ShopDTO shopDTO,
                          @RequestHeader("id") Long id) {
        log.info("商家{}添加商品{}", id, shopDTO);
        Long merchantId = id;
        shopClient.addShop(shopDTO, merchantId);
        return Result.success();
    }

    /**
     * 商家更新商品
     * @param shopUpdateDTO
     * @return
     */
    @PutMapping("/update")
    public Result updateShop(@RequestBody ShopUpdateDTO shopUpdateDTO){
        log.info("商家更新商品{}", shopUpdateDTO);
        shopClient.updateShop(shopUpdateDTO);
        return Result.success();
    }

    /**
     * 商家更新商品状态
     * @param shopUpdateDTO
     * @return
     */
    @PutMapping("/updateStatus")
    public Result updateShopStatus(@RequestBody ShopUpdateDTO shopUpdateDTO){
        log.info("商家更新商品状态{}", shopUpdateDTO);
        shopClient.updateShopStatus(shopUpdateDTO);
        return Result.success();
    }

    /**
     * 商家删除商品
     * @param shopId
     * @return
     */
    @DeleteMapping("/delete/{shopId}")
    public Result deleteShop(@PathVariable Long shopId){
        log.info("商家删除商品{}", shopId);
        shopClient.deleteShop(shopId);
        return Result.success();
    }

    /**
     * 商家查询自己的所有商品（分页）
     * @param merchantId
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return
     */
    @GetMapping("/byMerchantId")
    public Result<PageVO<ShopPO>> byMerchantId(@RequestHeader("id") Long merchantId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "12") Integer size){
        log.info("查询商家{}的商品，页码：{}，每页大小：{}", merchantId, page, size);
        List<ShopPO> list = shopClient.byMerchantId(merchantId);

        // 手动分页
        int total = list.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);

        List<ShopPO> pageData = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();

        PageVO<ShopPO> pageVO = new PageVO<>((long) total, pageData);
        return Result.success(pageVO);
    }

    /**
     * 根据id查询商品详情（全量数据）
     * @param shopId
     * @return
     */
    @GetMapping("/details/{shopId}")
    public Result<ShopDetailsVO> byShopIdDetails(@PathVariable Long shopId){
        log.info("查询商品详情{}", shopId);
        return Result.success(shopClient.byShopIdDetails(shopId));
    }

}
