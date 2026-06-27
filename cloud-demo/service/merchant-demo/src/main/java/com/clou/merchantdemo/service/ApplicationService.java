package com.clou.merchantdemo.service;

import com.clou.common.result.Result;
import com.clou.merchantdemo.POJO.vo.PageVO;

public interface ApplicationService {

    Result<Void> applyDeleteComment(String commentId, String reason, String phone, Long merchantId);

    Result<Void> applyRestoreProduct(Long productId, String reason, String phone, Long merchantId);

    Result<PageVO> getApplicationList(Long merchantId, Integer requestType, Integer requestStatus, Integer page, Integer size);

    Result<PageVO> getRefundApplicationList(Long merchantId, String orderId, Long userId, Integer page, Integer size);

    Result<Void> processRefundApplication(Long applicationId, Integer operationType, String feedback, Long merchantId);

}
