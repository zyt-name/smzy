package com.clou.admindemo.controller;

import com.clou.admindemo.POJO.DTO.ApplicationQueryDTO;
import com.clou.admindemo.POJO.DTO.ApplicationProcessDTO;
import com.clou.admindemo.POJO.vo.ApplicationAggregateVO;
import com.clou.admindemo.POJO.vo.ApplicationVO;
import com.clou.admindemo.POJO.vo.PageVO;
import com.clou.admindemo.service.ApplicationService;
import com.clou.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/application")
public class AdminApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/list")
    public Result<PageVO<ApplicationVO>> getApplicationList(ApplicationQueryDTO queryDTO) {
        log.info("分页查询申请列表: {}", queryDTO);
        return Result.success(applicationService.getApplicationList(queryDTO));
    }

    @GetMapping("/aggregate")
    public Result<ApplicationAggregateVO> getApplicationAggregate() {
        log.info("获取申请聚合数据");
        return Result.success(applicationService.getApplicationAggregate());
    }

    @PostMapping("/process")
    public Result<Void> processApplication(@RequestBody ApplicationProcessDTO processDTO) {
        log.info("处理申请: {}", processDTO);
        applicationService.processApplication(processDTO);
        return Result.success();
    }

}
