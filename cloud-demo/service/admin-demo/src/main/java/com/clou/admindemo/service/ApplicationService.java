package com.clou.admindemo.service;

import com.clou.admindemo.POJO.DTO.ApplicationQueryDTO;
import com.clou.admindemo.POJO.DTO.ApplicationProcessDTO;
import com.clou.admindemo.POJO.vo.ApplicationAggregateVO;
import com.clou.admindemo.POJO.vo.PageVO;
import com.clou.admindemo.POJO.vo.ApplicationVO;

public interface ApplicationService {

    PageVO<ApplicationVO> getApplicationList(ApplicationQueryDTO queryDTO);

    ApplicationAggregateVO getApplicationAggregate();

    void processApplication(ApplicationProcessDTO processDTO);

}
