package com.clou.admindemo.mapper;

import com.clou.admindemo.POJO.po.UserBanLogPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserBanLogMapper {

    @Insert("insert into user_ban_log(ban_id, ban_type, operator_id, operate_type, reason, create_time) " +
            "values(#{banId}, #{banType}, #{operatorId}, #{operateType}, #{reason}, #{createTime})")
    void insert(UserBanLogPO userBanLogPO);
}
