package com.clou.userdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFavoriteMerchantPO {

    private Integer id;

    private Long userId;

    private Long merchantId;

}