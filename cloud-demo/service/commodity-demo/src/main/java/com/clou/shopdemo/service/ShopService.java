package com.clou.shopdemo.service;

import com.clou.shopdemo.POJO.dto.AdminShopSearchDTO;
import com.clou.shopdemo.POJO.dto.ShopDTO;
import com.clou.shopdemo.POJO.dto.ShopEsDTO;
import com.clou.shopdemo.POJO.dto.ShopUpdateDTO;
import com.clou.shopdemo.POJO.po.ShopPO;
import com.clou.shopdemo.POJO.vo.PageVO;
import com.clou.shopdemo.POJO.vo.ShopDetailsVO;
import com.clou.shopdemo.POJO.vo.ShopEsVO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ShopService {

    // 商家添加商品
    void addShop(ShopDTO shopDTO,Long merchantId) throws IOException;
    // 商家修改商品
    void updateShop(ShopUpdateDTO shopUpdateDTO) throws IOException;
    // 上/下架商品（管理员和商家都可以操作）
    void updateShopStatus(ShopUpdateDTO shopUpdateDTO) throws IOException;

    // 管理员更新商品admin_status状态（强制下架/恢复正常）
    void updateShopAdminStatus(Long shopId, Integer adminStatus) throws IOException;

    // 删除商品
    void deleteShop(Long shopId) throws IOException;

    // 商家/管理员根据商家id查询商家的所有商品
    List<ShopPO> byMerchantId(Long merchantId);

    // 用户根据关键词搜索商品(商品名字/商品价格/商品分类) - 分页查询
    PageVO<ShopEsVO> searchShop(ShopEsDTO shopEsDTO) throws IOException;

    // 管理员根据关键词搜索商品(商品名字/商品分类/商家ID) - 分页查询（不过滤状态）
    PageVO<ShopEsVO> adminSearchShop(AdminShopSearchDTO adminShopSearchDTO);

    // 根据商品id查询商品详情
    ShopDetailsVO byShopIdDetails(Long shopId);

    // 根据商品id列表查询商品图片
    Map<Long, String> getImagesByProductIds(List<Long> productIds);

}
