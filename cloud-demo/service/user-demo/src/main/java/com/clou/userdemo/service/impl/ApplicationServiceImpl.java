package com.clou.userdemo.service.impl;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.ApplicationDTO;
import com.clou.userdemo.POJO.po.ApplicationPO;
import com.clou.userdemo.POJO.vo.ApplicationVO;
import com.clou.userdemo.POJO.vo.PageVO;
import com.clou.userdemo.mapper.ApplicationMapper;
import com.clou.userdemo.service.ApplicationService;
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

    private static final int REQUESTER_TYPE_USER = 1;
    private static final int TARGET_TYPE_ORDER = 1;
    private static final int TARGET_TYPE_MERCHANT = 2;
    private static final int TARGET_TYPE_PRODUCT = 3;
    private static final int REQUEST_TYPE_REFUND = 1;
    private static final int REQUEST_TYPE_REPORT_MERCHANT = 2;
    private static final int REQUEST_TYPE_REPORT_PRODUCT = 3;
    private static final int REQUEST_STATUS_PROCESSING = 1;

    @Override
    public Result<Void> applyRefund(ApplicationDTO applicationDTO, Long userId) {
        log.info("用户申请退款: userId={}, orderId={}", userId, applicationDTO.getTargetId());

        ApplicationPO applicationPO = new ApplicationPO();
        applicationPO.setUserId(userId);
        applicationPO.setRequesterType(REQUESTER_TYPE_USER);
        applicationPO.setPhone(applicationDTO.getPhone());
        applicationPO.setTargetId(applicationDTO.getTargetId());
        applicationPO.setTargetType(TARGET_TYPE_ORDER);
        applicationPO.setRequestType(REQUEST_TYPE_REFUND);
        applicationPO.setCreateTime(LocalDateTime.now());
        applicationPO.setRequestStatus(REQUEST_STATUS_PROCESSING);
        applicationPO.setReason(applicationDTO.getReason());

        applicationMapper.insertApplication(applicationPO);
        log.info("退款申请提交成功: applicationId={}", applicationPO.getId());

        return Result.success();
    }

    @Override
    public Result<Void> reportMerchant(ApplicationDTO applicationDTO, Long userId) {
        log.info("用户举报商家: userId={}, merchantId={}", userId, applicationDTO.getTargetId());

        ApplicationPO applicationPO = new ApplicationPO();
        applicationPO.setUserId(userId);
        applicationPO.setRequesterType(REQUESTER_TYPE_USER);
        applicationPO.setPhone(applicationDTO.getPhone());
        applicationPO.setTargetId(applicationDTO.getTargetId());
        applicationPO.setTargetType(TARGET_TYPE_MERCHANT);
        applicationPO.setRequestType(REQUEST_TYPE_REPORT_MERCHANT);
        applicationPO.setCreateTime(LocalDateTime.now());
        applicationPO.setRequestStatus(REQUEST_STATUS_PROCESSING);
        applicationPO.setReason(applicationDTO.getReason());

        applicationMapper.insertApplication(applicationPO);
        log.info("商家举报提交成功: applicationId={}", applicationPO.getId());

        return Result.success();
    }

    @Override
    public Result<Void> reportProduct(ApplicationDTO applicationDTO, Long userId) {
        log.info("用户举报商品: userId={}, productId={}", userId, applicationDTO.getTargetId());

        ApplicationPO applicationPO = new ApplicationPO();
        applicationPO.setUserId(userId);
        applicationPO.setRequesterType(REQUESTER_TYPE_USER);
        applicationPO.setPhone(applicationDTO.getPhone());
        applicationPO.setTargetId(applicationDTO.getTargetId());
        applicationPO.setTargetType(TARGET_TYPE_PRODUCT);
        applicationPO.setRequestType(REQUEST_TYPE_REPORT_PRODUCT);
        applicationPO.setCreateTime(LocalDateTime.now());
        applicationPO.setRequestStatus(REQUEST_STATUS_PROCESSING);
        applicationPO.setReason(applicationDTO.getReason());

        applicationMapper.insertApplication(applicationPO);
        log.info("商品举报提交成功: applicationId={}", applicationPO.getId());

        return Result.success();
    }

    @Override
    public Result<PageVO> getApplicationList(Long userId, Integer requestType, Integer requestStatus, Integer page, Integer size) {
        log.info("查询用户申请列表: userId={}, requestType={}, requestStatus={}, page={}, size={}",
                userId, requestType, requestStatus, page, size);

        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : size;
        int offset = (page - 1) * size;

        List<ApplicationPO> applicationPOList = applicationMapper.selectApplicationList(
                userId, REQUESTER_TYPE_USER, requestType, requestStatus, offset, size);

        Long total = applicationMapper.countApplicationList(
                userId, REQUESTER_TYPE_USER, requestType, requestStatus);

        List<ApplicationVO> applicationVOList = new ArrayList<>();
        for (ApplicationPO applicationPO : applicationPOList) {
            ApplicationVO applicationVO = new ApplicationVO();
            BeanUtils.copyProperties(applicationPO, applicationVO);
            applicationVOList.add(applicationVO);
        }

        PageVO pageVO = new PageVO();
        pageVO.setCount(total.intValue());
        pageVO.setData(applicationVOList);

        return Result.success(pageVO);
    }

}
