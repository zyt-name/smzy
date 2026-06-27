package com.clou.apidemo.client;

import com.clou.apidemo.POJO.dto.AdminShopSearchDTO;
import com.clou.apidemo.POJO.dto.ShopDTO;
import com.clou.apidemo.POJO.dto.ShopEsDTO;
import com.clou.apidemo.POJO.dto.ShopUpdateDTO;
import com.clou.apidemo.POJO.po.ShopPO;
import com.clou.apidemo.POJO.vo.PageVO;
import com.clou.apidemo.POJO.vo.ShopDetailsVO;
import com.clou.apidemo.POJO.vo.ShopEsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "shop-demo")
public interface ShopClient {

    /**
     * 商家添加商品
     * @param shopDTO
     * @param merchantId
     */
    @PostMapping("/shop-demo/shop/add")
    void addShop(@RequestBody ShopDTO shopDTO,
                 @RequestParam("merchantId") Long merchantId);

    /**
     * 商家修改商品
     * @param shopUpdateDTO
     */
    @PutMapping("/shop-demo/shop/update")
    void updateShop(@RequestBody ShopUpdateDTO shopUpdateDTO);

    /**
     * 管理员/商家修改商品状态
     * @param shopUpdateDTO
     */
    @PutMapping("/shop-demo/shop/updateStatus")
    void updateShopStatus(@RequestBody ShopUpdateDTO shopUpdateDTO);

    /**
     * 管理员更新商品admin_status状态（强制下架/恢复正常）
     * @param shopId 商品ID
     * @param adminStatus 管理员状态（0-正常，1-强制下架）
     */
    @PutMapping("/shop-demo/shop/updateAdminStatus")
    void updateShopAdminStatus(@RequestParam("shopId") Long shopId,
                               @RequestParam("adminStatus") Integer adminStatus);

    /**
     * 商家删除商品
     * @param shopId
     */
    @DeleteMapping("/shop-demo/shop/delete")
    void deleteShop(@RequestParam("shopId") Long shopId);

    /**
     * 管理员/商家根据商家id查询商品
     * @param merchantId
     * @return
     */
    @GetMapping("/shop-demo/shop/byMerchantId")
    List<ShopPO> byMerchantId(@RequestParam("merchantId") Long merchantId);

    /**
     * 用户查询商品 - 分页查询
     * @param shopEsDTO
     * @return
     */
    @PostMapping("/shop-demo/shop/search")
    PageVO<ShopEsVO> searchShop(@RequestBody ShopEsDTO shopEsDTO);

    /**
     * 管理员查询商品 - 分页查询（不过滤状态，查看所有商品）
     * @param adminShopSearchDTO
     * @return
     */
    @PostMapping("/shop-demo/shop/admin/search")
    PageVO<ShopEsVO> adminSearchShop(@RequestBody AdminShopSearchDTO adminShopSearchDTO);

    /**
     * 根据商品id查询商品详情（管理员和商家查看全量数据，用户查看部分数据）
     * @param shopId
     * @return
     */
    @GetMapping("/shop-demo/shop/details")
    ShopDetailsVO byShopIdDetails(@RequestParam Long shopId);

    /**
     * 按商家ID同步商品到ES
     * @param merchantId
     */
    @PostMapping("/shop-demo/shop/sync/byMerchant")
    void syncByMerchant(@RequestParam("merchantId") Long merchantId);

    /**
     * 按商品ID同步单个商品到ES
     * @param shopId 商品ID
     */
    @PostMapping("/shop-demo/shop/sync/byShopId")
    void syncByShopId(@RequestParam("shopId") Long shopId);

    /**
     * 从ES中删除商品
     * @param shopId 商品ID
     */
    @DeleteMapping("/shop-demo/shop/sync/byShopId")
    void deleteFromEs(@RequestParam("shopId") Long shopId);

}
