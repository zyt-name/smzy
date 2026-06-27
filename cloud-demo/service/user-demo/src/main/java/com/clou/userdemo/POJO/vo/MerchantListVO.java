package com.clou.userdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantListVO {

    private Integer id;

    private String username;

    private String merchantNo;

    private String phone;

    private Integer status;

    private LocalDateTime createdAt;

}
