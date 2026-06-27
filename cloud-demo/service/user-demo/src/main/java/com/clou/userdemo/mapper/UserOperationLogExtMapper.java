package com.clou.userdemo.mapper;

import com.clou.servicecommon.pojo.UserOperationLogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserOperationLogExtMapper {

    @Select("SELECT * FROM user_operation_log WHERE user_id = #{userId} AND create_time >= #{startTime}")
    List<UserOperationLogPO> getByUserIdAndTimeAfter(Long userId, LocalDateTime startTime);

    @Select("SELECT * FROM user_operation_log WHERE user_id = #{userId}")
    List<UserOperationLogPO> getByUserId(Long userId);

    @Select("SELECT DISTINCT user_id FROM user_operation_log")
    List<Long> getAllUserIds();

}
