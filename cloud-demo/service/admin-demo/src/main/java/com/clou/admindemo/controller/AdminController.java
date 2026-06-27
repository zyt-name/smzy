package com.clou.admindemo.controller;

import com.clou.admindemo.POJO.DTO.AdminDTO;
import com.clou.admindemo.POJO.DTO.AdminLoginDTO;
import com.clou.admindemo.POJO.DTO.BanOperateDTO;
import com.clou.admindemo.POJO.vo.AdminMerchantVO;
import com.clou.admindemo.POJO.vo.AdminUserVO;
import com.clou.admindemo.POJO.vo.AdminVO;
import com.clou.admindemo.service.AdminService;
import com.clou.apidemo.client.MerchantClient;
import com.clou.apidemo.client.UserClient;
import com.clou.servicecommon.annotation.LogAnnotation;
import com.clou.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    AdminService adminService;
    @Autowired
    UserClient userClient;
    @Autowired
    MerchantClient merchantClient;

    /**
     * 添加管理员
     * @param adminDTO
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody AdminDTO adminDTO) {
        log.info("添加管理员: {}", adminDTO);
        adminService.addAdmin(adminDTO);
        return Result.success();
    }

    /**
     * 管理员登陆
     * @param adminDTO
     * @return
     */
    @PostMapping("/login")
    @LogAnnotation("管理员登录")
    public Result<AdminVO> login(@RequestBody AdminLoginDTO adminDTO) {
        log.info("管理员登陆: {}", adminDTO);
        return Result.success(adminService.login(adminDTO));
    }

    /**
     * 更新管理员
     * @param adminDTO
     * @param id
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody AdminDTO adminDTO
                        ,@RequestHeader("id") Long id) {
        log.info("更新管理员: {}, id: {}", adminDTO, id);
        adminService.updateAdmin(adminDTO, id);
        return Result.success();
    }

    /**
     * 更新用户状态
     * @param userId
     * @param status
     * @return
     */
    @PutMapping("/updateUserStatus")
    public Result updateUserStatus(@RequestParam Long userId,
                                   @RequestParam Integer status) {
        log.info("更新用户状态: {}, id: {}, status: {}", userId, status);
        if (userClient.adminUpdateUserStatus(userId, status)){
            return Result.success();
        }else {
            return Result.error("更新用户状态失败");
        }
    }

    @PutMapping("/updateAdminStatus")
    public Result updateAdminStatus(@RequestParam Long merchantId,
                                    @RequestParam Integer status) {
        log.info("更新管理员状态:  id: {}, status: {}", merchantId, status);
        if (merchantClient.updateMerchantStatus(merchantId, status)){
            return Result.success();
        }else {
            return Result.error("更新管理员状态失败");
        }
    }

    /**
     * 管理员退出
     * @param token
     * @return
     */
    @PostMapping("/exit")
    @LogAnnotation("管理员退出")
    public Result exit(@RequestHeader("token") String token) {
        log.info("管理员退出: {}", token);
        adminService.logout(token);
        return Result.success();
    }

    /**
     * 显示用户列表
     * @return
     */
    @GetMapping("/showUser")
    public Result<List<AdminUserVO>> showUser(@RequestParam int pageNum,
                                              @RequestParam int pageSize){
        log.info("管理员查看用户列表: {},{}", pageNum, pageSize);
        return Result.success(adminService.showUser(pageNum, pageSize));
    }

    /**
     * 显示商户列表
     * @return
     */
    @GetMapping("/showMerchant")
    public Result<List<AdminMerchantVO>> showMerchant(@RequestParam int pageNum,
                                                      @RequestParam int pageSize){
        log.info("管理员查看商户列表");
        return Result.success(adminService.showMerchant(pageNum, pageSize));
    }

    /**
     * 封禁/解封用户
     * @param banOperateDTO
     * @param adminId
     * @return
     */
    @PostMapping("/banUser")
    @LogAnnotation("管理员封禁/解封用户")
    public Result banUser(@RequestBody BanOperateDTO banOperateDTO,
                          @RequestHeader("id") Long adminId) {
        log.info("管理员封禁/解封用户: {}, 操作类型: {}, 原因: {}",
                banOperateDTO.getTargetId(), banOperateDTO.getOperateType(), banOperateDTO.getReason());
        adminService.banOrUnbanUser(banOperateDTO, adminId);
        String operateName = banOperateDTO.getOperateType() == 1 ? "封禁" : "解封";
        return Result.success(operateName + "用户成功");
    }

    /**
     * 封禁/解封商家
     * @param banOperateDTO
     * @param adminId
     * @return
     */
    @PostMapping("/banMerchant")
    @LogAnnotation("管理员封禁/解封商家")
    public Result banMerchant(@RequestBody BanOperateDTO banOperateDTO,
                              @RequestHeader("id") Long adminId) {
        log.info("管理员封禁/解封商家: {}, 操作类型: {}, 原因: {}",
                banOperateDTO.getTargetId(), banOperateDTO.getOperateType(), banOperateDTO.getReason());
        adminService.banOrUnbanMerchant(banOperateDTO, adminId);
        String operateName = banOperateDTO.getOperateType() == 1 ? "封禁" : "解封";
        return Result.success(operateName + "商家成功");
    }

}
