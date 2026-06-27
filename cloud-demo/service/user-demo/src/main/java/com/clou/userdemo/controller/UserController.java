package com.clou.userdemo.controller;

import com.clou.servicecommon.annotation.LogAnnotation;
import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.UserDTO;
import com.clou.userdemo.POJO.dto.UserLoginDTO;
import com.clou.userdemo.POJO.vo.UserVO;
import com.clou.userdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户注册
     * @param userDTO
     * @return
     */
    @PostMapping("/addUser")
    public Result AddUser(@RequestBody UserDTO userDTO) {
        log.info("用户注册:{}", userDTO);
        userService.addUser(userDTO);
        return Result.success();
    }

    /**
     * 用户登录
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    @LogAnnotation("用户登录")
    public Result<UserVO> login(@RequestBody UserLoginDTO userDTO) {
        log.info("用户登录:{}", userDTO);
        UserVO userVO = userService.login(userDTO);
        return Result.success(userVO);
    }

    /**
     * 用户更新
     * @param userDTO
     * @param id
     * @return
     */
    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody UserDTO userDTO,
                             @RequestHeader Long id) {
        log.info("用户更新:{}{}", userDTO, id);
        userService.updateUser(userDTO, id);
        return Result.success();
    }

    /**
     * 管理员更新用户状态
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/admin/updateUserStatus")
    public boolean adminUpdateUserStatus(@RequestParam("userId") Long id,
                                         @RequestParam("status") int status) {
        log.info("管理员更新用户状态:{}{}", id, status);
        return userService.adminUpdateUserStatus(id, status);
    }

    @PostMapping("/exit")
    @LogAnnotation("用户退出")
    public Result exit(@RequestHeader("token") String token){
        log.info("用户退出:{}", token);
        userService.logout(token);
        return Result.success();
    }

}
