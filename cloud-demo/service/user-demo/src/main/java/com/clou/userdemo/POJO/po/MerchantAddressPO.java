package com.clou.userdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantAddressPO {

    private Integer id;

    private Integer merchantId;

    private String province;

    private LocalDateTime createdTime;

    private String city;

    private String district;

    private String detailAddress;

    private LocalDateTime updateTime;

}