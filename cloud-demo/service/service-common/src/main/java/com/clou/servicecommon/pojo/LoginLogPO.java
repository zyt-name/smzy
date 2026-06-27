package com.clou.servicecommon.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginLogPO {

    private String userName;
    private String userNo;
    private String operation;
    private LocalDateTime operateTime;

}
