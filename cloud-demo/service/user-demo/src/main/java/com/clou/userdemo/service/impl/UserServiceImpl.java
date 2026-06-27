package com.clou.userdemo.service.impl;

import com.clou.common.exception.BusinessException;
import com.clou.common.util.JwtUtil;
import com.clou.common.util.NoGenerator;
import com.clou.common.util.SaTokenHelper;
import com.clou.servicecommon.util.PasswordUtil;
import com.clou.userdemo.POJO.dto.UserDTO;
import com.clou.userdemo.POJO.dto.UserLoginDTO;
import com.clou.userdemo.POJO.po.UserBanLogPO;
import com.clou.userdemo.POJO.po.UserPO;
import com.clou.userdemo.POJO.vo.UserVO;
import com.clou.userdemo.mapper.UserBanLogMapper;
import com.clou.userdemo.mapper.UserMapper;
import com.clou.userdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;

import static com.clou.common.constant.code.errorCode;
import static com.clou.common.constant.code.unAuthCode;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserBanLogMapper userBanLogMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private View error;

    /**
     * 添加用户
     * @param userDTO
     */
    @Override
    public void addUser(UserDTO userDTO) {
        UserPO userPO = new UserPO();
        BeanUtils.copyProperties(userDTO, userPO);
        userPO.setStatus(0);
        userPO.setUserNo("300"+ NoGenerator.generate10UniqueNum());
        userPO.setCreatedAt(LocalDateTime.now());
        userPO.setPassword(PasswordUtil.encode(userDTO.getPassword()));
        userMapper.insertUser(userPO);
    }

    /**
     * 登录
     * @param userDTO
     * @return
     */
    @Override
    public UserVO login(UserLoginDTO userDTO) {
        UserPO userPO = userMapper.getUserByUserNo(userDTO.getUserNo());
        log.info("查询到的用户信息：{}", userPO);
        if (userPO == null){
            throw new BusinessException(errorCode,"账号或者密码错误");
        }
        log.info("开始验证密码，输入密码：{}，数据库密码：{}", userDTO.getPassword(), userPO.getPassword());
        boolean matches = PasswordUtil.matches(userDTO.getPassword(), userPO.getPassword());
        log.info("密码验证结果：{}", matches);
        if (!matches) {
            throw new BusinessException(errorCode,"账号或者密码错误");
        }
        if (userPO.getStatus() == 1){ // 封禁
            UserBanLogPO banLog = userBanLogMapper.getLatestUnbanLog(userPO.getId(), 1);
            String reason = banLog != null && banLog.getReason() != null ? banLog.getReason() : "无";
            throw new BusinessException(unAuthCode, "账号被封禁，封禁原因：" + reason + "，请联系管理员！");
        }
        UserVO userVO = new UserVO();
        userVO.setId(userPO.getId());
        userVO.setUserName(userPO.getUsername());
        String token= SaTokenHelper.loginAndGenerateToken("user", userPO.getId(), userPO.getUsername(), userPO.getUserNo());
        userVO.setToken(token);
        // 查询页面路径
        String pagePath = userMapper.getPagePathByCode("user_page");
        userVO.setPagePath(pagePath);
        log.info("生成的信息：{}", userVO);
        return userVO;
    }

    /**
     * 更新用户信息
     * @param userDTO
     */
    @Override
    public void updateUser(UserDTO userDTO, Long id) {

        UserPO userPO = new UserPO();
        BeanUtils.copyProperties(userDTO, userPO);
        userPO.setId(id);
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            userPO.setPassword(PasswordUtil.encode(userDTO.getPassword()));
        }
        userMapper.updateUser(userPO);
    }

    /**
     * 管理员更新用户状态
     * @param id
     * @param status
     */
    @Override
    public boolean adminUpdateUserStatus(Long id, int status) {
        return userMapper.adminUpdateUserStatus(id, status);
    }

    /**
     * 登出
     * @param token
     */
    @Override
    public void logout(String token) {
        String tokenKey = "satoken:login:token:" + token;
        // 获取 loginId (如 user:3)
        String loginId = stringRedisTemplate.opsForValue().get(tokenKey);
        log.info("用户退出登录，token: {}, loginId: {}", token, loginId);
        if (loginId != null) {
            // 删除 session 信息 (satoken:login:session:user:3)
            String sessionKey = "satoken:login:session:" + loginId;
            stringRedisTemplate.delete(sessionKey);
            log.info("已删除 session 缓存: {}", sessionKey);
        }
        // 删除 token 映射 (satoken:login:token:xxx)
        stringRedisTemplate.delete(tokenKey);
        log.info("已删除 token 缓存: {}", tokenKey);
    }


}
