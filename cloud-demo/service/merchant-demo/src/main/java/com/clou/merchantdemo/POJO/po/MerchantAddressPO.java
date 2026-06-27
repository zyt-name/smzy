package com.clou.merchantdemo.POJO.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantAddressPO {

    // 省份
    private String province;
    // 城市
    private String city;
    // 区/县
    private String district;
    // 详细地址
    private String detailAddress;
    // 商家id
    private Long merchantId;
    // 创建时间
    private LocalDateTime createTime;
    // 修改时间
    private LocalDateTime updateTime;

}
