package com.clou.merchantdemo.POJO.dto;

import lombok.Data;

@Data
public class MerchantAddressDTO {

    // 省份
    private String province;
    // 城市
    private String city;
    // 区/县
    private String district;
    // 详细地址
    private String detailAddress;

}
