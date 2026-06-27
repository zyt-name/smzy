package com.clou.admindemo.POJO.DTO;

import lombok.Data;

@Data
public class BanOperateDTO {

    private Long targetId;
    private String reason;
    private Integer operateType;
}
