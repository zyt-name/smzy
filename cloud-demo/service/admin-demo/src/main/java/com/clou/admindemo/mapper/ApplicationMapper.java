package com.clou.admindemo.mapper;

import com.clou.admindemo.POJO.DTO.ApplicationQueryDTO;
import com.clou.admindemo.POJO.DTO.ApplicationProcessDTO;
import com.clou.admindemo.POJO.vo.ApplicationAggregateVO;
import com.clou.admindemo.POJO.vo.ApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApplicationMapper {

    List<ApplicationVO> getApplicationList(ApplicationQueryDTO queryDTO);

    Long getApplicationTotal(ApplicationQueryDTO queryDTO);

    @Select("SELECT COUNT(*) FROM application WHERE request_type = 3 AND request_status = 1")
    Long getGoodsReportCount();

    @Select("SELECT COUNT(*) FROM application WHERE request_type = 2 AND request_status = 1")
    Long getMerchantReportCount();

    @Select("SELECT COUNT(*) FROM application WHERE request_type = 1 AND request_status = 1 AND create_time < DATE_SUB(NOW(), INTERVAL 1 DAY)")
    Long getRefundTimeoutCount();

    @Select("SELECT COUNT(*) FROM application WHERE request_type = 4 AND request_status = 1")
    Long getDeleteCommentCount();

    int updateApplicationStatus(ApplicationProcessDTO processDTO);

}
