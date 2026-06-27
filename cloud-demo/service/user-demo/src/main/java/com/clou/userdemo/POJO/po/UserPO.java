package com.clou.userdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPO {

    private Long id;
    private String username;
    private String password;
    private String phone;
    // 0表示正常，1表示冻结
    private int status;
    private LocalDateTime createdAt;
    // 用户编号，300+10位唯一的随机数
    private String userNo;

}
