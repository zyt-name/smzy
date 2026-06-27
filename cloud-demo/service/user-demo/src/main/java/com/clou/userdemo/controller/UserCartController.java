package com.clou.userdemo.controller;

import com.clou.servicecommon.annotation.UserOperationLog;
import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.UserCartDTO;
import com.clou.userdemo.POJO.vo.UserCartVO;
import com.clou.userdemo.service.UserCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/cart")
@Slf4j
public class UserCartController {

    @Autowired
    UserCartService userCartService;

    /**
     * 添加购物车
     * @param userCartDTO
     * @param userId
     * @return
     */
    @RequestMapping("/addCart")
    @UserOperationLog(operateType = 2)
    public Result addCart(@RequestBody UserCartDTO userCartDTO,
                          @RequestHeader("id") Long userId) {
        log.info("用户{}添加购物车{}", userId, userCartDTO);
        userCartService.addCart(userCartDTO, userId);
        return Result.success();
    }

    /**
     * 查询购物车
     * @param userId
     * @return
     */
    @GetMapping("/showCart")
    @UserOperationLog(operateType = 1)
    public Result<List<UserCartVO>> showCart(@RequestHeader("id") Long userId) {
        log.info("用户{}查看购物车", userId);
        return Result.success(userCartService.getCart(userId));
    }

    /**
     * 查询购物车（给订单服务调用）
     * @param userId
     * @return
     */
    @GetMapping("/showCartClient")
    public List<UserCartVO> showCartClient(@RequestParam("userId") Long userId) {
        log.info("用户{}查看购物车", userId);
        return userCartService.getCart(userId);
    }

    /**
     * 更改购物车中商品数量
     * @param userCartDTO
     * @param userId
     * @return
     */
    @PutMapping("/updateCartCount")
    public Result updateCartCount(@RequestBody UserCartDTO userCartDTO,
                                  @RequestHeader("id") Long userId) {
        log.info("用户{}更新购物车商品数量{}", userId, userCartDTO);
        userCartService.updateCartCount(userCartDTO, userId);
        return Result.success();
    }

    /**
     * 清空购物车
     * @param userId
     * @return
     */
    @DeleteMapping("/deleteCart")
    public Result deleteCart(@RequestHeader("id") Long userId) {
        log.info("用户{}清空购物车", userId);
        userCartService.deleteCart(userId);
        return Result.success();
    }

    /**
     * 删除购物车中单个商品
     * @param userCartDTO 包含productId和userSpecification
     * @param userId
     * @return
     */
    @DeleteMapping("/deleteCartItem")
    public Result deleteCartItem(@RequestBody UserCartDTO userCartDTO,
                                @RequestHeader("id") Long userId) {
        log.info("用户{}删除购物车商品{}", userId, userCartDTO);
        userCartService.deleteCartItem(userCartDTO, userId);
        return Result.success();
    }


}
