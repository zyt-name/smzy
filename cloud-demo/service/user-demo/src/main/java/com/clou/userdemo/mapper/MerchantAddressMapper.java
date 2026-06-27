package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.po.MerchantAddressPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MerchantAddressMapper {

    @Select("SELECT * FROM merchant_address WHERE merchant_id = #{merchantId}")
    List<MerchantAddressPO> selectByMerchantId(Integer merchantId);

    @Select("<script>" +
            "SELECT * FROM merchant_address WHERE merchant_id IN " +
            "<foreach item='item' index='index' collection='merchantIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<MerchantAddressPO> selectByMerchantIds(List<Integer> merchantIds);

}