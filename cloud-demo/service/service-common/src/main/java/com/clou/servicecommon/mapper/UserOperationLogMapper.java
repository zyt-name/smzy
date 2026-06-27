package com.clou.servicecommon.mapper;

import com.clou.servicecommon.pojo.UserOperationLogPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserOperationLogMapper {

    @Insert("INSERT INTO user_operation_log (user_id, operate_type, operate_data, create_time) " +
            "VALUES (#{userId}, #{operateType}, #{operateData}, #{createTime})")
    void insert(UserOperationLogPO logPO);

}
