package com.clou.merchantdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantPO {

    private Long id;
    private String username;
    private String password;
    // 0：正常，1：冻结
    private int status;
    private LocalDateTime createAt;
    // 商家编号，200+10随机数，唯一
    private String merchantNo;
    // 商家电话
    private String phone;

}
