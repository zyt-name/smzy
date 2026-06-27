package com.clou.merchantdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantVO {

    private Long id;
    private String userName;
    private String token;
    private String pagePath;

}
