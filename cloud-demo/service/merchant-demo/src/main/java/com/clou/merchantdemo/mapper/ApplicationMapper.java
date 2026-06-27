package com.clou.merchantdemo.mapper;

import com.clou.merchantdemo.POJO.po.ApplicationPO;
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

    List<ApplicationPO> selectRefundApplicationList(@Param("merchantId") Long merchantId,
                                                     @Param("requesterType") Integer requesterType,
                                                     @Param("targetType") Integer targetType,
                                                     @Param("requestType") Integer requestType,
                                                     @Param("orderId") String orderId,
                                                     @Param("userId") Long userId,
                                                     @Param("offset") Integer offset,
                                                     @Param("pageSize") Integer pageSize);

    Long countRefundApplicationList(@Param("merchantId") Long merchantId,
                                     @Param("requesterType") Integer requesterType,
                                     @Param("targetType") Integer targetType,
                                     @Param("requestType") Integer requestType,
                                     @Param("orderId") String orderId,
                                     @Param("userId") Long userId);

    void updateApplicationStatus(@Param("applicationId") Long applicationId,
                                  @Param("requestStatus") Integer requestStatus,
                                  @Param("feedback") String feedback);

}
