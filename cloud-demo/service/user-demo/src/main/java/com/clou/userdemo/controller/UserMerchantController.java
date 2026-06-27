package com.clou.userdemo.controller;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.vo.FavoriteMerchantVO;
import com.clou.userdemo.POJO.vo.MerchantInfoVO;
import com.clou.userdemo.POJO.vo.PageVO;
import com.clou.userdemo.service.UserFavoriteMerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/merchant")
@Slf4j
public class UserMerchantController {

    @Autowired
    UserFavoriteMerchantService userFavoriteMerchantService;

    /**
     * 用户收藏商家
     * @param merchantId 商家ID
     * @param userId 用户ID
     * @return Result
     */
    @PostMapping("/favorite/{merchantId}")
    public Result addFavoriteMerchant(@PathVariable Long merchantId,
                                      @RequestHeader("id") Long userId) {
        log.info("用户{}收藏商家{}", userId, merchantId);
        userFavoriteMerchantService.addFavoriteMerchant(userId, merchantId);
        return Result.success();
    }

    /**
     * 用户取消收藏商家
     * @param merchantId 商家ID
     * @param userId 用户ID
     * @return Result
     */
    @DeleteMapping("/favorite/{merchantId}")
    public Result deleteFavoriteMerchant(@PathVariable Long merchantId,
                                         @RequestHeader("id") Long userId) {
        log.info("用户{}取消收藏商家{}", userId, merchantId);
        userFavoriteMerchantService.deleteFavoriteMerchant(userId, merchantId);
        return Result.success();
    }

    /**
     * 用户查看收藏商家列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param userId 用户ID
     * @return Result<PageVO<FavoriteMerchantVO>>
     */
    @GetMapping("/favorite/list")
    public Result<PageVO> getFavoriteMerchantList(
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize,
            @RequestHeader("id") Long userId) {
        log.info("用户{}查看收藏商家列表 pageNum={} pageSize={}", userId, pageNum, pageSize);
        return Result.success(userFavoriteMerchantService.getFavoriteMerchantList(userId, pageNum, pageSize));
    }

    /**
     * 查询当前用户是否已收藏指定商家
     * @param merchantId 商家ID
     * @param userId 用户ID
     * @return Result<Boolean>
     */
    @GetMapping("/favorite/{merchantId}/check")
    public Result<Boolean> isFavorite(@PathVariable Long merchantId,
                                     @RequestHeader("id") Long userId) {
        log.info("用户{}查询是否已收藏商家{}", userId, merchantId);
        return Result.success(userFavoriteMerchantService.isFavorite(userId, merchantId));
    }

    /**
     * 查询指定商家信息
     * @param merchantId 商家ID
     * @return Result<MerchantInfoVO>
     */
    @GetMapping("/info/{merchantId}")
    public Result<MerchantInfoVO> getMerchantInfo(@PathVariable Long merchantId) {
        log.info("查询商家信息{}", merchantId);
        return Result.success(userFavoriteMerchantService.getMerchantInfo(merchantId));
    }

}