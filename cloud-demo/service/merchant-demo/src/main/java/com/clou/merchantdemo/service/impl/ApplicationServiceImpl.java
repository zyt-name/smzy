package com.clou.merchantdemo.service.impl;

import com.clou.common.result.Result;
import com.clou.merchantdemo.POJO.po.ApplicationPO;
import com.clou.merchantdemo.POJO.vo.ApplicationVO;
import com.clou.merchantdemo.POJO.vo.PageVO;
import com.clou.merchantdemo.mapper.ApplicationMapper;
import com.clou.merchantdemo.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    private static final int REQUESTER_TYPE_MERCHANT = 2;
    private static final int REQUESTER_TYPE_USER = 1;
    private static final int TARGET_TYPE_COMMENT = 4;
    private static final int TARGET_TYPE_PRODUCT = 3;
    private static final int TARGET_TYPE_ORDER = 1;
    private static final int REQUEST_TYPE_DELETE_COMMENT = 4;
    private static final int REQUEST_TYPE_RESTORE_PRODUCT = 5;
    private static final int REQUEST_TYPE_REFUND = 1;
    private static final int REQUEST_STATUS_PROCESSING = 1;
    private static final int REQUEST_STATUS_REJECTED = 2;
    private static final int REQUEST_STATUS_APPROVED = 3;

    @Override
    public Result<Void> applyDeleteComment(String commentId, String reason, String phone, Long merchantId) {
        log.info("商家申请删除评论: merchantId={}, commentId={}", merchantId, commentId);

        ApplicationPO applicationPO = new ApplicationPO();
        applicationPO.setUserId(merchantId);
        applicationPO.setRequesterType(REQUESTER_TYPE_MERCHANT);
        applicationPO.setPhone(phone != null ? phone : "");
        applicationPO.setTargetId(commentId);
        applicationPO.setTargetType(TARGET_TYPE_COMMENT);
        applicationPO.setRequestType(REQUEST_TYPE_DELETE_COMMENT);
        applicationPO.setCreateTime(LocalDateTime.now());
        applicationPO.setRequestStatus(REQUEST_STATUS_PROCESSING);
        applicationPO.setReason(reason);

        applicationMapper.insertApplication(applicationPO);
        log.info("删除评论申请提交成功: applicationId={}", applicationPO.getId());

        return Result.success();
    }

    @Override
    public Result<Void> applyRestoreProduct(Long productId, String reason, String phone, Long merchantId) {
        log.info("商家申请恢复商品: merchantId={}, productId={}", merchantId, productId);

        ApplicationPO applicationPO = new ApplicationPO();
        applicationPO.setUserId(merchantId);
        applicationPO.setRequesterType(REQUESTER_TYPE_MERCHANT);
        applicationPO.setPhone(phone != null ? phone : "");
        applicationPO.setTargetId(productId.toString());
        applicationPO.setTargetType(TARGET_TYPE_PRODUCT);
        applicationPO.setRequestType(REQUEST_TYPE_RESTORE_PRODUCT);
        applicationPO.setCreateTime(LocalDateTime.now());
        applicationPO.setRequestStatus(REQUEST_STATUS_PROCESSING);
        applicationPO.setReason(reason);

        applicationMapper.insertApplication(applicationPO);
        log.info("恢复商品申请提交成功: applicationId={}", applicationPO.getId());

        return Result.success();
    }

    @Override
    public Result<PageVO> getApplicationList(Long merchantId, Integer requestType, Integer requestStatus, Integer page, Integer size) {
        log.info("查询商家申请列表: merchantId={}, requestType={}, requestStatus={}, page={}, size={}",
                merchantId, requestType, requestStatus, page, size);

        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : size;
        int offset = (page - 1) * size;

        List<ApplicationPO> applicationPOList = applicationMapper.selectApplicationList(
                merchantId, REQUESTER_TYPE_MERCHANT, requestType, requestStatus, offset, size);

        Long total = applicationMapper.countApplicationList(
                merchantId, REQUESTER_TYPE_MERCHANT, requestType, requestStatus);

        List<ApplicationVO> applicationVOList = new ArrayList<>();
        for (ApplicationPO applicationPO : applicationPOList) {
            ApplicationVO applicationVO = new ApplicationVO();
            BeanUtils.copyProperties(applicationPO, applicationVO);
            applicationVOList.add(applicationVO);
        }

        PageVO<ApplicationVO> pageVO = new PageVO<>();
        pageVO.setTotal(total);
        pageVO.setData(applicationVOList);

        return Result.success(pageVO);
    }

    @Override
    public Result<PageVO> getRefundApplicationList(Long merchantId, String orderId, Long userId, Integer page, Integer size) {
        log.info("查询退款申请列表: merchantId={}, orderId={}, userId={}, page={}, size={}",
                merchantId, orderId, userId, page, size);

        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : size;
        int offset = (page - 1) * size;

        List<ApplicationPO> applicationPOList = applicationMapper.selectRefundApplicationList(
                merchantId, REQUESTER_TYPE_USER, TARGET_TYPE_ORDER, REQUEST_TYPE_REFUND,
                orderId, userId, offset, size);

        Long total = applicationMapper.countRefundApplicationList(
                merchantId, REQUESTER_TYPE_USER, TARGET_TYPE_ORDER, REQUEST_TYPE_REFUND,
                orderId, userId);

        List<ApplicationVO> applicationVOList = new ArrayList<>();
        for (ApplicationPO applicationPO : applicationPOList) {
            ApplicationVO applicationVO = new ApplicationVO();
            BeanUtils.copyProperties(applicationPO, applicationVO);
            applicationVOList.add(applicationVO);
        }

        PageVO<ApplicationVO> pageVO = new PageVO<>();
        pageVO.setTotal(total);
        pageVO.setData(applicationVOList);

        return Result.success(pageVO);
    }

    @Override
    public Result<Void> processRefundApplication(Long applicationId, Integer operationType, String feedback, Long merchantId) {
        log.info("处理退款申请: applicationId={}, operationType={}, merchantId={}", applicationId, operationType, merchantId);

        Integer requestStatus;
        if (operationType == 1) {
            requestStatus = REQUEST_STATUS_APPROVED;
        } else if (operationType == 2) {
            requestStatus = REQUEST_STATUS_REJECTED;
        } else {
            return Result.error("无效的操作类型");
        }

        applicationMapper.updateApplicationStatus(applicationId, requestStatus, feedback != null ? feedback : "");
        log.info("退款申请处理成功: applicationId={}, status={}", applicationId, requestStatus);

        return Result.success();
    }

}
