package com.clou.admindemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminVO {

    private Long id;
    private String userName;
    private String token;
    private String pagePath;

}
