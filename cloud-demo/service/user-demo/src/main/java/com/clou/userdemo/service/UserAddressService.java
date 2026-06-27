package com.clou.userdemo.service;

import com.clou.userdemo.POJO.dto.UserAddressDTO;
import com.clou.userdemo.POJO.dto.UserAddressUpdateDTO;
import com.clou.userdemo.POJO.vo.UserAddressVO;


import java.util.List;

public interface UserAddressService {

    // 添加地址
    void addAddress (UserAddressDTO userAddressDTO, Long userId);

    // 修改默认地址
    void updateIsDefault (Long addressId, Long userId);

    // 获取用户地址
    List<UserAddressVO> getUserAddress (Long userId);

    // 修改地址
    void updateAddress (UserAddressUpdateDTO userAddressUpdateDTO);

    // 删除地址
    void deleteAddress (Long addressId);

    UserAddressVO byId (Long addressId);

}
