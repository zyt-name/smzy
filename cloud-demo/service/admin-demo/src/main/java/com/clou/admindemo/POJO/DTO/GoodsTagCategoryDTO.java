package com.clou.admindemo.POJO.DTO;

import lombok.Data;

@Data
public class GoodsTagCategoryDTO {

    private String tagName;
    private Integer tagType;
    private Long parentTagId;
    private String remark;
    private Integer level;
}
