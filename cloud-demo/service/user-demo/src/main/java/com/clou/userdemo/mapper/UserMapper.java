package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.dto.UserDTO;
import com.clou.userdemo.POJO.dto.UserLoginDTO;
import com.clou.userdemo.POJO.po.UserPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Insert("insert into users (id, username, password, phone, status, created_at, user_no) values (#{id}, #{username}, #{password}, #{phone}, #{status}, #{createdAt}, #{userNo})")
    void insertUser(UserPO userPO);

    @Select("select * from users where user_no = #{userNo}")
    UserPO getUserByUserNo(String userNo);

    @Select("select page_path from sys_page_permission where page_code = #{pageCode}")
    String getPagePathByCode(String pageCode);


    // 用户信息更新
    void updateUser(UserPO userPO);

    // 管理员更新用户状态
    @Update("update users set status = #{status} where id = #{id}")
    boolean adminUpdateUserStatus(Long id, Integer status);

}
