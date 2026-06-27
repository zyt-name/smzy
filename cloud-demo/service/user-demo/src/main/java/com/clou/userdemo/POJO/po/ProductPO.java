package com.clou.userdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPO {

    private Long id;
    private String name;
    private float price;
    private Long stock;
    private Long merchantId;
    private int status;
    private String category;
    private LocalDateTime createdAt;

}
