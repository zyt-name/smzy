package com.clou.userdemo.service;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.ApplicationDTO;
import com.clou.userdemo.POJO.vo.ApplicationVO;
import com.clou.userdemo.POJO.vo.PageVO;

public interface ApplicationService {

    Result<Void> applyRefund(ApplicationDTO applicationDTO, Long userId);

    Result<Void> reportMerchant(ApplicationDTO applicationDTO, Long userId);

    Result<Void> reportProduct(ApplicationDTO applicationDTO, Long userId);

    Result<PageVO> getApplicationList(Long userId, Integer requestType, Integer requestStatus, Integer page, Integer size);

}
