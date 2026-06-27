package com.clou.merchantdemo.mapper;

import com.clou.merchantdemo.POJO.dto.MerchantDTO;
import com.clou.merchantdemo.POJO.dto.MerchantLoginDTO;
import com.clou.merchantdemo.POJO.po.MerchantAddressPO;
import com.clou.merchantdemo.POJO.po.MerchantPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MerchantMapper {

    @Insert("insert into merchants(id, username, password, status, created_at, merchant_no) values(#{id}, #{username}, #{password}, #{status}, #{createAt}, #{merchantNo})")
    void insertMerchant(MerchantPO merchantPO);

    @Select("select * from merchants where merchant_no=#{merchantNo}")
    MerchantPO getMerchantByMerchantNo(String merchantNo);

    @Select("select page_path from sys_page_permission where page_code = #{pageCode}")
    String getPagePathByCode(String pageCode);

    void updateMerchant(MerchantPO merchantPO);

    // 管理员修改商户状态
    @Update("update merchants set status=#{status} where id=#{id}")
    boolean adminUpdateMerchantStatus(Long id, Integer status);

    // 添加商户地址
    @Insert("insert into merchant_address(merchant_id, province, city, district, detail_address,created_time,update_time) " +
            "values(#{merchantId}, #{province}, #{city}, #{district}, #{detailAddress}, #{createTime}, #{updateTime})")
    void addAddress(MerchantAddressPO merchantAddressPO);

    // 根据ID查询商户信息
    MerchantPO getMerchantById(Long id);

    // 根据商户ID查询地址列表
    List<MerchantAddressPO> getAddressListByMerchantId(Long merchantId);

}
