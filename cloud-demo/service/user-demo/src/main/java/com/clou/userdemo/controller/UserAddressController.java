package com.clou.userdemo.controller;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.UserAddressDTO;
import com.clou.userdemo.POJO.dto.UserAddressUpdateDTO;
import com.clou.userdemo.POJO.vo.UserAddressVO;
import com.clou.userdemo.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
@Slf4j
public class UserAddressController {

    @Autowired
    UserAddressService userAddressService;

    /**
     * 添加收货地址
     * @param userAddressDTO
     * @param userId
     * @return
     */
    @PostMapping("/add")
    public Result addAddress(@RequestBody UserAddressDTO userAddressDTO,
                             @RequestHeader("id") Long userId) {
        log.info("用户{}添加收获地址:{}",  userId, userAddressDTO);
        userAddressService.addAddress(userAddressDTO, userId);
        return Result.success();
    }

    /**
     * 更新默认地址
     * @param addressId
     * @param userId
     * @return
     */
    @PutMapping("/default/{addressId}")
    public Result updateIsDefault(@PathVariable("addressId") Long addressId,
                                  @RequestHeader("id") Long userId) {
        log.info("用户{}更新默认地址:{}",  userId, addressId);
        userAddressService.updateIsDefault(addressId, userId);
        return Result.success();
    }

    /**
     * 查看收货地址
     * @param userId
     * @return
     */
    @GetMapping("/list")
    public Result<List<UserAddressVO>> getUserAddress(@RequestHeader("id") Long userId) {
        log.info("用户{}查询收获地址",  userId);
        List<UserAddressVO> userAddressVOList = userAddressService.getUserAddress(userId);
        return Result.success(userAddressVOList);
    }

    /**
     * 更新收货地址
     * @param userAddressUpdateDTO
     * @return
     */
    @PutMapping("/update")
    public Result updataAddress(@RequestBody UserAddressUpdateDTO userAddressUpdateDTO) {
        log.info("用户更新收获地址:{}",  userAddressUpdateDTO);
        userAddressService.updateAddress(userAddressUpdateDTO);
        return Result.success();
    }

    /**
     * 删除收货地址
     * @param addressId
     * @return
     */
    @DeleteMapping("/delete/{addressId}")
    public Result deleteAddress(@PathVariable("addressId") Long addressId) {
        log.info("用户删除收获地址:{}",  addressId);
        userAddressService.deleteAddress(addressId);
        return Result.success();
    }

    /**
     * 根据id查询收货地址(api接口)
     * @param addressId
     * @return
     */
    @GetMapping("/byId")
    public UserAddressVO getUserAddressById(@RequestParam("addressId") Long addressId) {
        log.info("根据id查询收获地址:{}",  addressId);
        return userAddressService.byId(addressId);
    }

}
