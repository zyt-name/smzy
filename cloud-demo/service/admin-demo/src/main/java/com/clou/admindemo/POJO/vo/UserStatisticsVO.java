package com.clou.admindemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsVO {

    private List<String> dates;
    private List<Integer> newUserCounts;
    private List<Integer> activeUserCounts;
}
