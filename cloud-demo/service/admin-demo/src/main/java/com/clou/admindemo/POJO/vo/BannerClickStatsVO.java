package com.clou.admindemo.POJO.vo;

import lombok.Data;

@Data
public class BannerClickStatsVO {

    private String bannerId;

    private String title;

    private Integer clickCount;

    private Double clickRate;
}
