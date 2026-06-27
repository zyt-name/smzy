package com.clou.servicecommon.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOperationLogPO {

    private Long id;
    
    private Long userId;
    
    private Integer operateType;
    
    private String operateData;
    
    private LocalDateTime createTime;

}
