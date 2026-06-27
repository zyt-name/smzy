package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.po.UserFavoriteMerchantPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteMerchantMapper {

    @Select("SELECT * FROM user_favorite_merchant WHERE user_id = #{userId} AND merchant_id = #{merchantId}")
    UserFavoriteMerchantPO selectByUserIdAndMerchantId(@Param("userId") Long userId, @Param("merchantId") Long merchantId);

    @Insert("INSERT INTO user_favorite_merchant (user_id, merchant_id) VALUES (#{userId}, #{merchantId})")
    void insert(UserFavoriteMerchantPO userFavoriteMerchantPO);

    @Delete("DELETE FROM user_favorite_merchant WHERE user_id = #{userId} AND merchant_id = #{merchantId}")
    void deleteByUserIdAndMerchantId(@Param("userId") Long userId, @Param("merchantId") Long merchantId);

    @Select("SELECT * FROM user_favorite_merchant WHERE user_id = #{userId}")
    List<UserFavoriteMerchantPO> selectByUserId(Long userId);

}