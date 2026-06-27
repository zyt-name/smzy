package com.clou.admindemo.mapper;

import com.clou.admindemo.POJO.po.AdminPO;
import com.clou.admindemo.POJO.vo.AdminMerchantVO;
import com.clou.admindemo.POJO.vo.AdminUserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {

    //   添加管理员
    @Insert("insert into admins(id, username, password, created_at, admin_no) values(#{id}, #{username}, #{password}, #{createdAt}, #{adminNo})")
    void insert(AdminPO adminPO);

    //   根据管理员编号查询
    @Select("select * from admins where admin_no = #{adminNo}")
    AdminPO getAdminByAdminNo(String adminNo);

    //  获取页面路径
    @Select("select page_path from sys_page_permission where page_code = #{pageCode}")
    String getPagePathByCode(String pageCode);

    //  更新管理员
    void update(AdminPO adminPO);

    //  用户列表
    List<AdminUserVO> getUserList(int pageNum, int pageSize);

    //  商户列表
    List<AdminMerchantVO> getMerchantList(int pageNum, int pageSize);
}
