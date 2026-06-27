package com.clou.admindemo.POJO.DTO;

import lombok.Data;

@Data
public class BannerDTO {

    private String title;

    private String imagePath;

    private Long goodsId;

    private Integer sortOrder;
}
