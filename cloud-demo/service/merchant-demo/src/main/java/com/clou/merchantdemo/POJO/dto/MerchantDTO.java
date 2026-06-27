package com.clou.merchantdemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantDTO {

    private String username;
    private String password;
    private String phone;

}
