package com.clou.admindemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelStatsVO {

    private Integer categoryCount;
    private Integer categoryEnabledCount;
    private Integer tagCount;
    private Integer level1Count;
    private Integer level2Count;
    private Integer level3Count;
    private Integer tagEnabledCount;
}
