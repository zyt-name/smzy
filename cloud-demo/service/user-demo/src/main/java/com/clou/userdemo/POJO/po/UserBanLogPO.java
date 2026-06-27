package com.clou.userdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBanLogPO {

    private Long id;
    private Long banId;
    private Integer banType;
    private Long operatorId;
    private Integer operateType;
    private String reason;
    private LocalDateTime createTime;

}
