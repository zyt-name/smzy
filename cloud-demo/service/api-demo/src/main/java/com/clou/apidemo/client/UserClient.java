package com.clou.apidemo.client;

import com.clou.apidemo.POJO.vo.UserAddressVO;
import com.clou.apidemo.POJO.vo.UserCartVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-demo")
public interface UserClient {

    /**
     * 管理员更新用户状态
     * @param userId
     * @param status
     * @return
     */
    @PutMapping("/user-demo/users/admin/updateUserStatus")
    Boolean adminUpdateUserStatus(@RequestParam("userId") Long userId,
                                  @RequestParam("status") int status);


    /**
     * 获取用户购物车信息
     * @param userId
     * @return
     */
    @GetMapping("/user-demo/user/cart/showCartClient")
    List<UserCartVO> showCartClient(@RequestParam("userId") Long userId);

    /**
     * 获取用户地址信息
     * @param addressId
     * @return
     */
    @GetMapping("/user-demo/user/address/byId")
    UserAddressVO getUserAddressById(@RequestParam("addressId") Long addressId);

}
