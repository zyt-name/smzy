package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.po.UserBanLogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserBanLogMapper {

    @Select("SELECT * FROM user_ban_log WHERE ban_id = #{banId} AND ban_type = #{banType} AND operate_type = 2 ORDER BY create_time DESC LIMIT 1")
    UserBanLogPO getLatestUnbanLog(@Param("banId") Long banId, @Param("banType") Integer banType);

}
