package com.clou.merchantdemo.service;

import com.clou.merchantdemo.POJO.dto.MerchantAddressDTO;
import com.clou.merchantdemo.POJO.dto.MerchantDTO;
import com.clou.merchantdemo.POJO.dto.MerchantLoginDTO;
import com.clou.merchantdemo.POJO.po.MerchantAddressPO;
import com.clou.merchantdemo.POJO.po.MerchantPO;
import com.clou.merchantdemo.POJO.vo.MerchantVO;

import java.util.List;

public interface MerchantService {

    //  添加商户
    void AddMerchant(MerchantDTO merchantDTO);

    //  商户登录
    MerchantVO login(MerchantLoginDTO merchantDTO);

    //  商户信息修改
    void updateMerchant(MerchantDTO merchantDTO,Long id);

    //  商户状态修改
    boolean adminUpdateMerchantStatus(Long id,int status);

    //  商户地址添加
    void addAddress(MerchantAddressDTO merchantAddressDTO,Long id);

    //  退出登录
    void logout(String token);

    //  根据ID查询商户信息
    MerchantPO getMerchantById(Long id);

    //  根据商户ID查询地址列表
    List<MerchantAddressPO> getAddressListByMerchantId(Long merchantId);

}
