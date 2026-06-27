package com.clou.admindemo.service.impl;

import com.clou.admindemo.POJO.DTO.ApplicationQueryDTO;
import com.clou.admindemo.POJO.DTO.ApplicationProcessDTO;
import com.clou.admindemo.POJO.vo.ApplicationAggregateVO;
import com.clou.admindemo.POJO.vo.ApplicationVO;
import com.clou.admindemo.POJO.vo.PageVO;
import com.clou.admindemo.mapper.ApplicationMapper;
import com.clou.admindemo.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Override
    public PageVO<ApplicationVO> getApplicationList(ApplicationQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null) {
            queryDTO.setPageSize(10);
        }
        
        List<ApplicationVO> list = applicationMapper.getApplicationList(queryDTO);
        Long total = applicationMapper.getApplicationTotal(queryDTO);
        
        return new PageVO<>(total, list);
    }

    @Override
    public ApplicationAggregateVO getApplicationAggregate() {
        Long goodsReportCount = applicationMapper.getGoodsReportCount();
        Long merchantReportCount = applicationMapper.getMerchantReportCount();
        Long refundTimeoutCount = applicationMapper.getRefundTimeoutCount();
        Long deleteCommentCount = applicationMapper.getDeleteCommentCount();
        
        return new ApplicationAggregateVO(goodsReportCount, merchantReportCount, refundTimeoutCount, deleteCommentCount);
    }

    @Override
    public void processApplication(ApplicationProcessDTO processDTO) {
        applicationMapper.updateApplicationStatus(processDTO);
    }

}
