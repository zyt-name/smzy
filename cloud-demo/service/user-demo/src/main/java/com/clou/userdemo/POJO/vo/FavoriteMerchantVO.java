package com.clou.userdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteMerchantVO {

    private Integer id;

    private Long merchantId;

    private String merchantName;

    private String phone;

    private Integer status;

    private List<MerchantAddressVO> addressList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MerchantAddressVO {
        private String province;
        private String city;
        private String district;
        private String detailAddress;
    }

}