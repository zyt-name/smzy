package com.clou.userdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsPO {

    private Long id;
    private Long productId;
    private String description;
    private String specification;
    private String imageUrls;
    private LocalDateTime updatedAt;
    private String label;

}
