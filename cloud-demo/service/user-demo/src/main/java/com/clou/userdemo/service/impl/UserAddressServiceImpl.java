package com.clou.userdemo.service.impl;

import com.clou.common.exception.BusinessException;
import com.clou.userdemo.POJO.dto.UserAddressDTO;
import com.clou.userdemo.POJO.dto.UserAddressUpdateDTO;
import com.clou.userdemo.POJO.po.UserAddressPO;
import com.clou.userdemo.POJO.vo.UserAddressVO;
import com.clou.userdemo.mapper.UserAddressMapper;
import com.clou.userdemo.service.UserAddressService;
import com.clou.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    UserAddressMapper userAddressMapper;

    /**
     * 添加收货地址
     * @param userAddressDTO
     * @param userId
     */
    @Override
    public void addAddress(UserAddressDTO userAddressDTO, Long userId) {
        UserAddressPO userAddressPO = new UserAddressPO();
        userAddressPO.setUserId(userId);
        BeanUtils.copyProperties(userAddressDTO, userAddressPO);
        userAddressPO.setCreateTime(LocalDateTime.now());
        userAddressPO.setUpdateTime(LocalDateTime.now());
        // 添加收货地址
        userAddressMapper.insertUserAddress(userAddressPO);
    }

    /**
     * 修改默认地址
     * @param addressId
     * @param userId
     */
    @Override
    @Transactional
    public void updateIsDefault(Long addressId, Long userId) {
        // 查询旧默认地址
        UserAddressPO userAddressPO = userAddressMapper.byIsDefault(1, userId);
        if (userAddressPO != null) {
            // 把原默认地址修改为非默认(改为1)
            userAddressMapper.updateIsDefault(1, userAddressPO.getId());
        }
        // 把新地址修改为默认(改为0)
        userAddressMapper.updateIsDefault(0, addressId);
    }

    /**
     * 查看自己的所有收货地址
     * @param userId
     * @return
     */
    @Override
    public List<UserAddressVO> getUserAddress(Long userId) {
        List<UserAddressPO> userAddressPOList = userAddressMapper.selectAllUserAddress(userId);
        List<UserAddressVO> userAddressVOList = new ArrayList<>();

        // 把PO对象转换为VO对象
        for (UserAddressPO userAddressPO : userAddressPOList) {
            UserAddressVO userAddressVO = new UserAddressVO();
            BeanUtils.copyProperties(userAddressPO, userAddressVO);
            userAddressVOList.add(userAddressVO);
        }

        return userAddressVOList;
    }

    /**
     * 修改收货地址
     * @param userAddressUpdateDTO
     */
    @Override
    public void updateAddress(UserAddressUpdateDTO userAddressUpdateDTO) {
        UserAddressPO userAddressPO = new UserAddressPO();
        BeanUtils.copyProperties(userAddressUpdateDTO, userAddressPO);
        userAddressPO.setUpdateTime(LocalDateTime.now());
        userAddressMapper.updateUserAddress(userAddressPO);
    }

    /**
     * 删除收货地址
     * @param addressId
     */
    @Override
    public void deleteAddress(Long addressId) {
        UserAddressPO userAddressPO = userAddressMapper.byId(addressId);
        // 先判断地址是否存在
        if (userAddressPO == null) {
            throw new BusinessException(500, "地址不存在或已被删除");
        }
        // 再判断是否为默认地址
        if (userAddressPO.getIsDefault() == 0) {
            throw new BusinessException(500, "默认地址不能删除");
        }
        // 执行删除
        userAddressMapper.deleteUserAddress(addressId);
    }

    /**
     * 根据id查询收货地址
     * @param addressId
     * @return
     */
    @Override
    public UserAddressVO byId(Long addressId) {
        UserAddressPO userAddressPO = userAddressMapper.byId(addressId);
        UserAddressVO userAddressVO = new UserAddressVO();
        BeanUtils.copyProperties(userAddressPO, userAddressVO);
        return userAddressVO;
    }
}
