package com.clou.admindemo.POJO.DTO;

import lombok.Data;

@Data
public class BannerUpdateDTO {

    private String id;

    private String title;

    private String imagePath;

    private Long goodsId;

    private Integer sortOrder;
}
