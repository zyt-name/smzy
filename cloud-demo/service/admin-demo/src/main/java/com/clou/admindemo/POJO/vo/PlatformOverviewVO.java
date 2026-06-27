package com.clou.admindemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformOverviewVO {

    private Long totalUsers;
    private Long totalMerchants;
    private Long totalProducts;
    private Long totalOrders;
    private Long completedOrders;
    private Long bannedUsers;
    private Long bannedMerchants;
}
