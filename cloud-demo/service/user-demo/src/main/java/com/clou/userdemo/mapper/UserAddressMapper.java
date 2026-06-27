package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.po.UserAddressPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserAddressMapper {

    @Insert("insert into user_address(id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, create_time, update_time) " +
            "values(#{id}, #{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detailAddress}, #{isDefault}, #{createTime}, #{updateTime})")
    void insertUserAddress(UserAddressPO userAddressPO);

    //查看默认地址（看is_default是否为0）
    @Select("select * from user_address where is_default = 0 and user_id=#{userId}")
    UserAddressPO byIsDefault(Integer isDefault,Long userId);

    void updateUserAddress(UserAddressPO userAddressPO);

    @Update("UPDATE user_address SET is_default = #{isDefault} WHERE  id = #{id}")
    void updateIsDefault(int isDefault, Long id);

    @Select("SELECT * FROM user_address WHERE user_id = #{userId}")
    List<UserAddressPO> selectAllUserAddress(Long userId);

    @Delete("DELETE FROM user_address WHERE id = #{id}")
    void deleteUserAddress(Long id);

    @Select("select * from user_address where id = #{id}")
    UserAddressPO byId(Long id);

}
