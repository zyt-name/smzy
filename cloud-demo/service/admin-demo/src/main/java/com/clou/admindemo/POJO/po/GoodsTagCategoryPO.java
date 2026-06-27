package com.clou.admindemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsTagCategoryPO {

    private Long id;
    private String tagName;
    private Integer tagType;
    private Long parentTagId;
    private Integer status;
    private String remark;
    private String createdBy;
    private LocalDateTime createdTime;
    private Integer level;
}
