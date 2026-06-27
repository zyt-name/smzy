package com.clou.merchantdemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsTagQueryDTO {

    private Integer tagType;
    private Long parentTagId;
    private Integer level;

}
