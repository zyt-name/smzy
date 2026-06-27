package com.clou.merchantdemo.POJO.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationVO {

    private Long id;
    private Long userId;
    private String phone;
    private String targetId;
    private Integer targetType;
    private Integer requestType;
    private LocalDateTime createTime;
    private Integer requestStatus;
    private String reason;
    private String feedback;

}
