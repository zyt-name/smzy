package com.clou.admindemo.POJO.DTO;

import lombok.Data;

@Data
public class ApplicationQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    private Integer requesterType;

    private Long userId;

    private Integer requestStatus;

    private Integer requestType;

    private Long targetType;

    private String targetId;

}
