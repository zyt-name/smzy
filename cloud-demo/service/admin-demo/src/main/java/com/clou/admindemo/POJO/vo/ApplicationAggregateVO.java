package com.clou.admindemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationAggregateVO {

    private Long goodsReportCount;
    private Long merchantReportCount;
    private Long refundTimeoutCount;
    private Long deleteCommentCount;

}
