package com.clou.admindemo.POJO.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GoodsTagCategoryQueryDTO {

    private String tagName;

    private Integer status;

    private LocalDate createdTimeMonth;

    private Long parentTagId;

    private Integer level;

    private Integer tagType;
}
