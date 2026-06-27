package com.clou.userdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantPO {

    private Integer id;

    private String username;

    private String password;

    private Integer status;

    private LocalDateTime createdAt;

    private String merchantNo;

    private String phone;

}