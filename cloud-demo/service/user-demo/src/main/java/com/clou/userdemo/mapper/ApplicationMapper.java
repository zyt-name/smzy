package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.po.ApplicationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplicationMapper {

    void insertApplication(ApplicationPO applicationPO);

    List<ApplicationPO> selectApplicationList(@Param("userId") Long userId,
                                               @Param("requesterType") Integer requesterType,
                                               @Param("requestType") Integer requestType,
                                               @Param("requestStatus") Integer requestStatus,
                                               @Param("offset") Integer offset,
                                               @Param("pageSize") Integer pageSize);

    Long countApplicationList(@Param("userId") Long userId,
                               @Param("requesterType") Integer requesterType,
                               @Param("requestType") Integer requestType,
                               @Param("requestStatus") Integer requestStatus);

}
