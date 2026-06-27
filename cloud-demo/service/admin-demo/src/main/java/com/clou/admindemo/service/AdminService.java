package com.clou.admindemo.service;

import com.clou.admindemo.POJO.DTO.AdminDTO;
import com.clou.admindemo.POJO.DTO.AdminLoginDTO;
import com.clou.admindemo.POJO.DTO.BanOperateDTO;
import com.clou.admindemo.POJO.vo.AdminMerchantVO;
import com.clou.admindemo.POJO.vo.AdminUserVO;
import com.clou.admindemo.POJO.vo.AdminVO;

import java.util.List;

/**
 * @author LENOVO
 */
public interface AdminService {

    // 添加管理员
    void addAdmin(AdminDTO adminDTO);

    // 登录
    AdminVO login(AdminLoginDTO adminDTO);

    // 修改管理员
    void updateAdmin(AdminDTO adminDTO,Long id);

    // 退出登录
    void logout(String token);

    // 查看用户列表
    List<AdminUserVO> showUser(int pageNum, int pageSize);

    List<AdminMerchantVO> showMerchant(int pageNum, int pageSize);

    // 封禁/解封用户
    void banOrUnbanUser(BanOperateDTO banOperateDTO, Long operatorId);

    // 封禁/解封商家
    void banOrUnbanMerchant(BanOperateDTO banOperateDTO, Long operatorId);
}
