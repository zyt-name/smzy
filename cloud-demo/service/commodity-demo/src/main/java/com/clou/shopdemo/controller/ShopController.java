package com.clou.shopdemo.controller;

import com.clou.common.result.Result;
import com.clou.shopdemo.POJO.dto.AdminShopSearchDTO;
import com.clou.shopdemo.POJO.dto.ShopDTO;
import com.clou.shopdemo.POJO.dto.ShopEsDTO;
import com.clou.shopdemo.POJO.dto.ShopUpdateDTO;
import com.clou.shopdemo.POJO.po.ShopPO;
import com.clou.shopdemo.POJO.vo.PageVO;
import com.clou.shopdemo.POJO.vo.ShopDetailsVO;
import com.clou.shopdemo.POJO.vo.ShopEsVO;
import com.clou.shopdemo.service.ShopService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shop")
@Slf4j
public class ShopController {

    @Autowired
    ShopService shopService;

    /**
     * 商家添加商品
     * @param shopDTO
     * @param merchantId
     * @throws IOException
     */
    @PostMapping("/add")
    public void addShop(@RequestBody ShopDTO shopDTO,
                        @RequestParam Long merchantId) throws IOException {
        log.info("添加商品：{}", shopDTO);
        shopService.addShop(shopDTO, merchantId);
    }

    /**
     * 商家修改商品
     * @param shopUpdateDTO
     * @throws JsonProcessingException
     */
    @PutMapping("/update")
    public void updateShop(@RequestBody ShopUpdateDTO shopUpdateDTO ) throws IOException {
        log.info("更新商品：{}", shopUpdateDTO);
        shopService.updateShop(shopUpdateDTO);
    }

    /**
     * 管理员/商家修改商品状态（0：下架，1：上架）
     * @param shopUpdateDTO
     */
    @PutMapping("/updateStatus")
    public void updateShopStatus(@RequestBody ShopUpdateDTO shopUpdateDTO) throws IOException {
        log.info("更新商品状态：{}", shopUpdateDTO);
        shopService.updateShopStatus(shopUpdateDTO);
    }

    /**
     * 管理员更新商品admin_status状态（强制下架/恢复正常）
     * @param shopId 商品ID
     * @param adminStatus 管理员状态（0-正常，1-强制下架）
     */
    @PutMapping("/updateAdminStatus")
    public void updateShopAdminStatus(@RequestParam Long shopId,
                                      @RequestParam Integer adminStatus) throws IOException {
        log.info("管理员更新商品admin_status：shopId={}, adminStatus={}", shopId, adminStatus);
        shopService.updateShopAdminStatus(shopId, adminStatus);
    }

    /**
     * 商家删除商品
     * @param shopId
     */
    @DeleteMapping("/delete")
    public void deleteShop(@RequestParam Long shopId) throws IOException {
        log.info("删除商品：{}", shopId);
        shopService.deleteShop(shopId);
    }

    /**
     * 商家/管理员根据商家id查询商品
     * @param merchantId
     * @return
     */
    @GetMapping("/byMerchantId")
    public List<ShopPO> byMerchantId(@RequestParam("merchantId") Long merchantId) {
        log.info("查询商家商品：{}", merchantId);
        return shopService.byMerchantId(merchantId);
    }

    /**
     * 搜索商品 - 分页查询
     * @param shopEsDTO
     * @return
     * @throws IOException
     */
    @PostMapping("/search")
    public PageVO<ShopEsVO> searchShop(@RequestBody ShopEsDTO shopEsDTO) throws IOException {
        log.info("搜索商品：{}", shopEsDTO);
        PageVO<ShopEsVO> pageVO = shopService.searchShop(shopEsDTO);
        log.info("搜索结果：总数={}, 当前页数据量={}", pageVO.getTotal(), pageVO.getData().size());
        return pageVO;
    }

    /**
     * 管理员搜索商品 - 分页查询（不过滤状态，查看所有商品）
     * @param adminShopSearchDTO
     * @return
     */
    @PostMapping("/admin/search")
    public PageVO<ShopEsVO> adminSearchShop(@RequestBody AdminShopSearchDTO adminShopSearchDTO) {
        log.info("管理员搜索商品：{}", adminShopSearchDTO);
        PageVO<ShopEsVO> pageVO = shopService.adminSearchShop(adminShopSearchDTO);
        log.info("管理员搜索结果：总数={}, 当前页数据量={}", pageVO.getTotal(), pageVO.getData().size());
        return pageVO;
    }

    /**
     * 根据商品id查询商品详情
     * @param shopId
     * @return
     */
    @GetMapping("/details")
    public ShopDetailsVO byShopIdDetails(@RequestParam Long shopId) {
        log.info("查询商品详情：{}", shopId);
        return shopService.byShopIdDetails(shopId);
    }

    /**
     * 根据商品id列表查询商品图片
     * @param productIds
     * @return Map<商品id, 商品图片>
     */
    @PostMapping("/images")
    public Result<Map<Long, String>> getImagesByProductIds(@RequestBody List<Long> productIds) {
        log.info("查询商品图片：{}", productIds);
        Map<Long, String> imageMap = shopService.getImagesByProductIds(productIds);
        return Result.success(imageMap);
    }

}
