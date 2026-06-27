package com.clou.merchantdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationPO {

    private Long id;
    private Long userId;
    private Integer requesterType;
    private String phone;
    private String targetId;
    private Integer targetType;
    private Integer requestType;
    private LocalDateTime createTime;
    private Integer requestStatus;
    private String reason;
    private String feedback;

}
