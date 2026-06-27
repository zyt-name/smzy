package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.po.ProductDetailsPO;
import com.clou.userdemo.POJO.po.ProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT pd.* FROM product_details pd " +
            "INNER JOIN products p ON pd.product_id = p.id " +
            "WHERE pd.product_id = #{productId} AND p.status = 0 AND p.admin_status = 0")
    ProductDetailsPO getProductDetailsByProductId(Long productId);

    @Select("SELECT pd.* FROM product_details pd " +
            "INNER JOIN products p ON pd.product_id = p.id " +
            "WHERE p.status = 0 AND p.admin_status = 0")
    List<ProductDetailsPO> getAllProductDetails();

    @Select("SELECT * FROM products WHERE id = #{id} AND status = 0 AND admin_status = 0")
    ProductPO getProductById(Long id);

    @Select("SELECT * FROM products WHERE status = 0 AND admin_status = 0")
    List<ProductPO> getAllOnSaleProducts();

}
