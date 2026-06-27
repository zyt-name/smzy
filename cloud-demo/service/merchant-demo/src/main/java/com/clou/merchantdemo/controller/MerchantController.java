package com.clou.merchantdemo.controller;

import com.clou.servicecommon.annotation.LogAnnotation;
import com.clou.common.result.Result;
import com.clou.merchantdemo.POJO.dto.MerchantAddressDTO;
import com.clou.merchantdemo.POJO.dto.MerchantDTO;
import com.clou.merchantdemo.POJO.dto.MerchantLoginDTO;
import com.clou.merchantdemo.POJO.po.MerchantAddressPO;
import com.clou.merchantdemo.POJO.po.MerchantPO;
import com.clou.merchantdemo.POJO.vo.MerchantVO;
import com.clou.merchantdemo.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant")
@Slf4j
public class MerchantController {

    @Autowired
    MerchantService merchantService;

    /**
     * 商户注册
     * @param merchantDTO
     * @return
     */
    @PostMapping("/add")
    public Result addMerchant(@RequestBody MerchantDTO merchantDTO) {
        log.info("商户注册{}", merchantDTO);
        merchantService.AddMerchant(merchantDTO);
        return Result.success();
    }

    /**
     * 商户登录
     * @param merchantDTO
     * @return
     */
    @PostMapping("/login")
    @LogAnnotation("商户登录")
    public Result<MerchantVO> login(@RequestBody MerchantLoginDTO merchantDTO) {
        log.info("商户登录{}", merchantDTO);
        MerchantVO merchantVO = merchantService.login(merchantDTO);
        return Result.success(merchantVO);
    }

    /**
     * 商户更新
     * @param merchantDTO
     * @param id
     * @return
     */
    @PutMapping("/update")
    public Result updateMerchant(@RequestBody MerchantDTO merchantDTO,
                                 @RequestHeader("id") Long id) {
        log.info("商户编号{}，商户更新{}", id, merchantDTO);
        merchantService.updateMerchant(merchantDTO, id);
        return Result.success();
    }

    /**
     * 管理员更新商户状态(管理员调用)
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/admin/updateStatus")
    public boolean adminUpdateMerchantStatus(@RequestParam("merchantId") Long id,
                                             @RequestParam("status") Integer status) {
        log.info("管理员更新商户状态,merchantId:{},status:{}", id, status);
        return merchantService.adminUpdateMerchantStatus(id, status);
    }

    /**
     * 添加商户地址
     * @param merchantAddressDTO
     * @param id
     * @return
     */
    @PostMapping("/addAddress")
    public Result addAddress(@RequestBody MerchantAddressDTO merchantAddressDTO,
                             @RequestHeader("id") Long id){
        log.info("商户编号{}，添加地址{}",id,merchantAddressDTO);
            merchantService.addAddress(merchantAddressDTO,id);
            return Result.success();
    }

    /**
     * 商户退出
     * @param token
     * @return
     */
    @PostMapping("/exit")
    @LogAnnotation("商户退出")
    public Result exit(@RequestHeader("token") String token) {
        log.info("商户退出: {}", token);
        merchantService.logout(token);
        return Result.success();
    }

    /**
     * 获取商户信息
     * @param id
     * @return
     */
    @GetMapping("/info")
    public Result<MerchantPO> getMerchantInfo(@RequestHeader("id") Long id) {
        log.info("获取商户信息, id: {}", id);
        MerchantPO merchantPO = merchantService.getMerchantById(id);
        return Result.success(merchantPO);
    }

    /**
     * 获取商户地址列表
     * @param id
     * @return
     */
    @GetMapping("/addressList")
    public Result<List<MerchantAddressPO>> getAddressList(@RequestHeader("id") Long id) {
        log.info("获取商户地址列表, merchantId: {}", id);
        List<MerchantAddressPO> addressList = merchantService.getAddressListByMerchantId(id);
        return Result.success(addressList);
    }

}