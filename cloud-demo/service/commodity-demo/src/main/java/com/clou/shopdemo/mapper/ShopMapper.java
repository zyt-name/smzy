package com.clou.shopdemo.mapper;

import com.clou.shopdemo.POJO.dto.ShopUpdateDTO;
import com.clou.shopdemo.POJO.po.ShopDetailsPO;
import com.clou.shopdemo.POJO.po.ShopPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShopMapper {

    // 商品添加
    void addShop(ShopPO shopPO);

    // 商品详情添加
    @Insert("insert into product_details (product_id, description, specification, image_urls, label, updated_at) "+
            "values (#{shopId}, #{description}, #{specification}, #{imageUrls}, #{label}, #{updatedAt})")
    void addShopDetail(ShopDetailsPO shopDetailsPO);

    // 商品更新
    void updateShop(ShopUpdateDTO shopUpdateDTO);
    // 商品详情更新
    void updateShopDetail(ShopUpdateDTO shopUpdateDTO);

    // 管理员更新商品admin_status状态
    void updateShopAdminStatus(@Param("shopId") Long shopId, @Param("adminStatus") Integer adminStatus);

    // 根据商品id查商品
    @Select("select * from products where id = #{shopId}")
    ShopPO getShopById(int shopId);

    // 根据商品id查商品详情
    @Select("select * from product_details where product_id = #{shopId}")
    ShopDetailsPO getShopDetailsById(int shopId);

    // 删除商品
    @Delete("delete from products where id = #{shopId}")
    void deleteShop(Long shopId);

    // 删除商品详情
    @Delete("delete from product_details where product_id = #{shopId}")
    void deleteShopDetail(Long shopId);

    // 根据商家id查商品（关联商品详情表获取图片）
    List<ShopPO> byMerchantId(Long merchantId);

    // 查询所有商品
    @Select("select * from products")
    List<ShopPO> getAllShops();

    // 查询所有商品详情
    @Select("select * from product_details")
    @Results({
            @Result(column = "product_id", property = "shopId"),
            @Result(column = "image_urls", property = "imageUrls"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    List<ShopDetailsPO> getAllShopDetails();

    // 根据商品id列表查询商品图片
    List<ShopDetailsPO> getImagesByProductIds(@Param("productIds") List<Long> productIds);

    // 根据商家id查询所有商品
    @Select("select * from products where merchant_id = #{merchantId}")
    List<ShopPO> getShopsByMerchantId(Long merchantId);

    // 根据商品id列表查询商品详情
    List<ShopDetailsPO> getShopDetailsByShopIds(@Param("shopIds") List<Long> shopIds);

    // 管理员分页查询商品（支持动态条件）
    List<ShopPO> adminSearchShops(@Param("name") String name,
                                   @Param("merchantId") Long merchantId,
                                   @Param("category") String category,
                                   @Param("offset") int offset,
                                   @Param("pageSize") int pageSize);

    // 管理员查询商品总数（支持动态条件）
    long adminSearchShopsCount(@Param("name") String name,
                                @Param("merchantId") Long merchantId,
                                @Param("category") String category);

}
