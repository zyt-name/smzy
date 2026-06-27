package com.clou.admindemo.POJO.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BannerVO {

    private String id;

    private String title;

    private String imagePath;

    private Long goodsId;

    private Integer clickCount;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
