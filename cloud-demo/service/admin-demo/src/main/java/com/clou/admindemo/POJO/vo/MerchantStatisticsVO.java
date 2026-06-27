package com.clou.admindemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantStatisticsVO {

    private List<String> dates;
    private List<Integer> newMerchantCounts;
    private List<Integer> activeMerchantCounts;
}
