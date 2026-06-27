package com.clou.admindemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPO {

    private Long id;
    private String username;
    private String password;
    private LocalDateTime createdAt;
    //  管理员编号,100+10位随机数，唯一
    private String adminNo;

}
