package com.clou.userdemo.service;

import com.clou.userdemo.POJO.dto.UserDTO;
import com.clou.userdemo.POJO.dto.UserLoginDTO;
import com.clou.userdemo.POJO.po.UserPO;
import com.clou.userdemo.POJO.vo.UserVO;

public interface UserService {

    //  添加用户
    void addUser(UserDTO userDTO);

    //  用户登录
    UserVO login(UserLoginDTO userDTO);

    //  更新用户信息
    void updateUser(UserDTO userDTO, Long id);

    //  管理员更新用户状态
    boolean adminUpdateUserStatus(Long id, int status);

    //  退出登录
    void logout(String token);

}
