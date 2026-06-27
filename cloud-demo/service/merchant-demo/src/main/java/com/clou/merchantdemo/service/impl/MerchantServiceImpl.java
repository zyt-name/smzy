package com.clou.merchantdemo.service.impl;

import com.clou.common.exception.BusinessException;
import com.clou.common.util.JwtUtil;
import com.clou.common.util.NoGenerator;
import com.clou.common.util.SaTokenHelper;
import com.clou.merchantdemo.POJO.dto.MerchantAddressDTO;
import com.clou.merchantdemo.POJO.dto.MerchantDTO;
import com.clou.merchantdemo.POJO.dto.MerchantLoginDTO;
import com.clou.merchantdemo.POJO.po.MerchantAddressPO;
import com.clou.merchantdemo.POJO.po.MerchantPO;
import com.clou.merchantdemo.POJO.po.UserBanLogPO;
import com.clou.merchantdemo.POJO.vo.MerchantVO;
import com.clou.merchantdemo.mapper.MerchantMapper;
import com.clou.merchantdemo.mapper.UserBanLogMapper;
import com.clou.merchantdemo.service.MerchantService;
import com.clou.servicecommon.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.clou.common.constant.code.errorCode;
import static com.clou.common.constant.code.unAuthCode;

@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    UserBanLogMapper userBanLogMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加商户
     * @param merchantDTO
     */
    @Override
    public void AddMerchant(MerchantDTO merchantDTO) {
        MerchantPO merchantPO = new MerchantPO();
        BeanUtils.copyProperties(merchantDTO,merchantPO);
        // 默认设置为开启状态
        merchantPO.setStatus(0);
        merchantPO.setCreateAt(LocalDateTime.now());
        merchantPO.setMerchantNo("200"+ NoGenerator.generate10UniqueNum());
        merchantPO.setPassword(PasswordUtil.encode(merchantDTO.getPassword()));
        merchantMapper.insertMerchant(merchantPO);
    }

    /**
     * 商家登录
     * @param merchantDTO
     * @return
     */
    @Override
    public MerchantVO login(MerchantLoginDTO merchantDTO) {
        if (merchantDTO.getMerchantNo() == null || merchantDTO.getPassword() == null) {
            throw new BusinessException(errorCode, "用户名或密码不能为空");
        }
        MerchantPO merchantPO = merchantMapper.getMerchantByMerchantNo(merchantDTO.getMerchantNo());
        if (merchantPO == null){
            throw new BusinessException(errorCode,"用户名或密码错误");
        }
        if (!PasswordUtil.matches(merchantDTO.getPassword(), merchantPO.getPassword())) {
            throw new BusinessException(errorCode,"用户名或密码错误");
        }
        if (merchantPO.getStatus() == 1) {
            UserBanLogPO banLog = userBanLogMapper.getLatestUnbanLog(merchantPO.getId(), 2);
            String reason = banLog != null && banLog.getReason() != null ? banLog.getReason() : "无";
            throw new BusinessException(unAuthCode, "账号被封禁，封禁原因：" + reason + "，请联系管理员！");
        }
        MerchantVO merchantVO = new MerchantVO();
        BeanUtils.copyProperties(merchantPO,merchantVO);
        merchantVO.setId(merchantPO.getId());
        merchantVO.setUserName(merchantPO.getUsername());
        merchantVO.setToken(SaTokenHelper.loginAndGenerateToken("merchant", merchantPO.getId(), merchantPO.getUsername(), merchantPO.getMerchantNo()));
        // 查询页面路径
        String pagePath = merchantMapper.getPagePathByCode("merchant_page");
        merchantVO.setPagePath(pagePath);
        return merchantVO;
    }

    /**
     * 更新商户信息
     * @param merchantDTO
     * @param id
     */
    @Override
    public void updateMerchant(MerchantDTO merchantDTO,Long id) {
        MerchantPO merchantPO = new MerchantPO();
        BeanUtils.copyProperties(merchantDTO,merchantPO);
        merchantPO.setId(id);
        if (merchantDTO.getPassword() != null && !merchantDTO.getPassword().isEmpty()) {
            merchantPO.setPassword(PasswordUtil.encode(merchantDTO.getPassword()));
        }
        merchantMapper.updateMerchant(merchantPO);
    }

    /**
     * 管理员更新商户状态
     * @param id
     * @param status
     * @return
     */
    @Override
    public boolean adminUpdateMerchantStatus(Long id, int status) {
        return merchantMapper.adminUpdateMerchantStatus(id,status);
    }

    /**
     * 添加商户地址
     * @param merchantAddressDTO
     * @param id
     */
    @Override
    public void addAddress(MerchantAddressDTO merchantAddressDTO, Long id) {

        MerchantAddressPO merchantAddressPO = new MerchantAddressPO();
        BeanUtils.copyProperties(merchantAddressDTO,merchantAddressPO);
        merchantAddressPO.setMerchantId(id);
        merchantAddressPO.setCreateTime(LocalDateTime.now());
        merchantAddressPO.setUpdateTime(LocalDateTime.now());
        merchantMapper.addAddress(merchantAddressPO);
    }

    /**
     * 退出登录
     * @param token
     */
    @Override
    public void logout(String token) {
        String tokenKey = "satoken:login:token:" + token;
        String loginId = stringRedisTemplate.opsForValue().get(tokenKey);
        log.info("商家退出登录，token: {}, loginId: {}", token, loginId);
        if (loginId != null) {
            String sessionKey = "satoken:login:session:" + loginId;
            stringRedisTemplate.delete(sessionKey);
            log.info("已删除 merchant session 缓存: {}", sessionKey);
        }
        stringRedisTemplate.delete(tokenKey);
        log.info("已删除 merchant token 缓存: {}", tokenKey);
    }

    /**
     * 根据ID查询商户信息
     * @param id
     * @return
     */
    @Override
    public MerchantPO getMerchantById(Long id) {
        return merchantMapper.getMerchantById(id);
    }

    /**
     * 根据商户ID查询地址列表
     * @param merchantId
     * @return
     */
    @Override
    public List<MerchantAddressPO> getAddressListByMerchantId(Long merchantId) {
        return merchantMapper.getAddressListByMerchantId(merchantId);
    }
}
