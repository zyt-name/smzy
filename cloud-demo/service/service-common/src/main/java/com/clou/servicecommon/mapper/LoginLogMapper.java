package com.clou.servicecommon.mapper;

import com.clou.servicecommon.pojo.LoginLogPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper {

    @Insert("insert into login_log(user_name, user_no, operation, operate_time) " +
            "values(#{userName}, #{userNo}, #{operation}, #{operateTime})")
    public void insert(LoginLogPO loginLogPO);

}
