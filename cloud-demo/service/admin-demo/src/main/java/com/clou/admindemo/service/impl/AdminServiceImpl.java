package com.clou.admindemo.service.impl;

import com.clou.admindemo.POJO.DTO.AdminDTO;
import com.clou.admindemo.POJO.DTO.AdminLoginDTO;
import com.clou.admindemo.POJO.DTO.BanOperateDTO;
import com.clou.admindemo.POJO.po.AdminPO;
import com.clou.admindemo.POJO.po.UserBanLogPO;
import com.clou.admindemo.POJO.vo.AdminMerchantVO;
import com.clou.admindemo.POJO.vo.AdminUserVO;
import com.clou.admindemo.POJO.vo.AdminVO;
import com.clou.admindemo.mapper.AdminMapper;
import com.clou.admindemo.mapper.UserBanLogMapper;
import com.clou.admindemo.service.AdminService;
import com.clou.apidemo.client.MerchantClient;
import com.clou.apidemo.client.ShopClient;
import com.clou.apidemo.client.UserClient;
import com.clou.apidemo.POJO.dto.ShopUpdateDTO;
import com.clou.apidemo.POJO.po.ShopPO;
import com.clou.common.exception.BusinessException;
import com.clou.common.util.NoGenerator;
import com.clou.common.util.SaTokenHelper;
import com.clou.servicecommon.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.clou.common.constant.code.errorCode;

/**
 * @author LENOVO
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserBanLogMapper userBanLogMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private MerchantClient merchantClient;

    @Autowired
    private ShopClient shopClient;

    /**
     * 添加管理员
     * @param adminDTO
     */
    @Override
    public void addAdmin(AdminDTO adminDTO) {
        AdminPO adminPO = new AdminPO();
        BeanUtils.copyProperties(adminDTO, adminPO);
        adminPO.setCreatedAt(LocalDateTime.now());
        // 100+10位唯一的随机数
        adminPO.setAdminNo("100"+ NoGenerator.generate10UniqueNum());
        adminPO.setPassword(PasswordUtil.encode(adminDTO.getPassword()));
        adminMapper.insert(adminPO);
    }

    /**
     * 管理员登录
     * @param adminDTO
     * @return
     */
    @Override
    public AdminVO login(AdminLoginDTO adminDTO) {
        log.info("管理员登陆: {}", adminDTO);
        if (adminDTO.getAdminNo() == null || adminDTO.getPassword() == null){
            throw new BusinessException(errorCode, "账号或者密码不能位空");
        }
        AdminPO adminPO = adminMapper.getAdminByAdminNo(adminDTO.getAdminNo());
        if (adminPO == null){
            throw new BusinessException(errorCode, "账号或密码错误");
        }
        if (!PasswordUtil.matches(adminDTO.getPassword(), adminPO.getPassword())) {
            throw new BusinessException(errorCode, "账号或密码错误");
        }
        //  使用Sa-Token
        String token = SaTokenHelper.loginAndGenerateToken("admin", adminPO.getId(), adminPO.getUsername(), adminPO.getAdminNo());

        AdminVO adminVO = new AdminVO();
        adminVO.setUserName(adminPO.getUsername());
        adminVO.setId(adminPO.getId());
        adminVO.setToken(token);
        // 查询页面路径
        String pagePath = adminMapper.getPagePathByCode("admin_page");
        adminVO.setPagePath(pagePath);
        return adminVO;
    }

    /**
     * 管理员更新用户信息
     * @param adminDTO
     * @param id
     */
    @Override
    public void updateAdmin(AdminDTO adminDTO,Long id) {
            AdminPO adminPO = new AdminPO();
            BeanUtils.copyProperties(adminDTO, adminPO);
            adminPO.setId(id);
            if (adminDTO.getPassword() != null && !adminDTO.getPassword().isEmpty()) {
                adminPO.setPassword(PasswordUtil.encode(adminDTO.getPassword()));
            }
            adminMapper.update(adminPO);
    }

    /**
     * 退出登录
     * @param token
     */
    @Override
    public void logout(String token) {
        String tokenKey = "satoken:login:token:" + token;
        String loginId = stringRedisTemplate.opsForValue().get(tokenKey);
        log.info("管理员退出登录，token: {}, loginId: {}", token, loginId);
        if (loginId != null) {
            String sessionKey = "satoken:login:session:" + loginId;
            stringRedisTemplate.delete(sessionKey);
            log.info("已删除 admin session 缓存: {}", sessionKey);
        }
        stringRedisTemplate.delete(tokenKey);
        log.info("已删除 admin token 缓存: {}", tokenKey);
    }

    @Override
    public List<AdminUserVO> showUser(int pageNum, int pageSize) {
        return adminMapper.getUserList(pageNum, pageSize);
    }

    @Override
    public List<AdminMerchantVO> showMerchant(int pageNum, int pageSize) {
        return adminMapper.getMerchantList(pageNum, pageSize);
    }

    /**
     * 封禁/解封用户
     * @param banOperateDTO
     * @param operatorId
     */
    @Override
    public void banOrUnbanUser(BanOperateDTO banOperateDTO, Long operatorId) {
        log.info("管理员封禁/解封用户: {}, 操作类型: {}", banOperateDTO.getTargetId(), banOperateDTO.getOperateType());

        // 1. 更新用户状态 (1=封禁, 0=正常)
        int status = banOperateDTO.getOperateType() == 1 ? 1 : 0;
        boolean success = userClient.adminUpdateUserStatus(banOperateDTO.getTargetId(), status);
        if (!success) {
            throw new BusinessException(errorCode, "操作用户状态失败");
        }

        // 2. 记录操作日志
        UserBanLogPO logPO = new UserBanLogPO();
        logPO.setBanId(banOperateDTO.getTargetId());
        logPO.setBanType(1); // 1=用户
        logPO.setOperatorId(operatorId);
        logPO.setOperateType(banOperateDTO.getOperateType()); // 1=封禁, 2=解封
        logPO.setReason(banOperateDTO.getReason());
        logPO.setCreateTime(LocalDateTime.now());
        userBanLogMapper.insert(logPO);
    }

    /**
     * 封禁/解封商家
     * @param banOperateDTO
     * @param operatorId
     */
    @Override
    public void banOrUnbanMerchant(BanOperateDTO banOperateDTO, Long operatorId) {
        log.info("管理员封禁/解封商家: {}, 操作类型: {}", banOperateDTO.getTargetId(), banOperateDTO.getOperateType());

        // 1. 更新商家状态 (1=封禁, 0=正常)
        int status = banOperateDTO.getOperateType() == 1 ? 1 : 0;
        boolean success = merchantClient.updateMerchantStatus(banOperateDTO.getTargetId(), status);
        if (!success) {
            throw new BusinessException(errorCode, "操作商家状态失败");
        }

        // 2. 记录操作日志
        UserBanLogPO logPO = new UserBanLogPO();
        logPO.setBanId(banOperateDTO.getTargetId());
        logPO.setBanType(2); // 2=商家
        logPO.setOperatorId(operatorId);
        logPO.setOperateType(banOperateDTO.getOperateType()); // 1=封禁, 2=解封
        logPO.setReason(banOperateDTO.getReason());
        logPO.setCreateTime(LocalDateTime.now());
        userBanLogMapper.insert(logPO);

        // 3. 如果是封禁商家，下架所有商品；如果是解封商家，上架所有商品
        // 获取商家所有商品
        List<ShopPO> shopList = shopClient.byMerchantId(banOperateDTO.getTargetId());
        if (shopList != null && !shopList.isEmpty()) {
            int shopStatus = banOperateDTO.getOperateType() == 1 ? 1 : 0; // 1=下架, 0=上架
            for (ShopPO shop : shopList) {
                ShopUpdateDTO updateDTO = new ShopUpdateDTO();
                updateDTO.setId(shop.getId().longValue());
                updateDTO.setStatus(shopStatus);
                shopClient.updateShopStatus(updateDTO);
            }
            log.info("已更新商家{}的所有商品状态为: {}", banOperateDTO.getTargetId(), shopStatus == 1 ? "下架" : "上架");
        }

        // 4. 同步ES
        try {
            shopClient.syncByMerchant(banOperateDTO.getTargetId());
            log.info("已同步商家{}的商品到ES", banOperateDTO.getTargetId());
        } catch (Exception e) {
            log.error("同步商家商品到ES失败: {}", e.getMessage());
        }

        // 5. 清除相关缓存
        try {
            // 清除 recommend:user 开头的缓存
            clearCacheByPattern("recommend:user*");
            // 清除 searchShop 开头的缓存
            clearCacheByPattern("searchShop*");
            log.info("已清除商家相关缓存");
        } catch (Exception e) {
            log.error("清除缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 根据 pattern 清除 Redis 缓存
     * @param pattern key 匹配模式
     */
    private void clearCacheByPattern(String pattern) {
        try {
            // 使用 scan 查找匹配的 key
            org.springframework.data.redis.core.Cursor<byte[]> cursor = stringRedisTemplate.executeWithStickyConnection(
                    redisConnection -> redisConnection.scan(
                            org.springframework.data.redis.core.ScanOptions.scanOptions()
                                    .match(pattern)
                                    .count(100)
                                    .build()
                    )
            );

            if (cursor != null) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = new String(keyBytes);
                    stringRedisTemplate.delete(key);
                    log.debug("删除缓存 key: {}", key);
                }
                cursor.close();
            }
        } catch (Exception e) {
            log.error("清除缓存 pattern: {} 失败: {}", pattern, e.getMessage());
            throw e;
        }
    }
}
