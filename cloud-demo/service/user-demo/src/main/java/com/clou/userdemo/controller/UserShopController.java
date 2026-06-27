package com.clou.userdemo.controller;

import com.clou.apidemo.POJO.dto.ShopEsDTO;
import com.clou.apidemo.POJO.vo.PageVO;
import com.clou.apidemo.POJO.vo.ShopDetailsUserVO;
import com.clou.apidemo.POJO.vo.ShopEsVO;
import com.clou.apidemo.client.ShopClient;

import com.clou.common.result.Result;
import com.clou.servicecommon.annotation.UserOperationLog;
import com.clou.userdemo.mapper.CategoryMapper;
import com.clou.userdemo.mapper.MerchantMapper;
import com.clou.userdemo.POJO.po.MerchantPO;
import com.clou.userdemo.POJO.vo.CategoryVO;
import com.clou.userdemo.POJO.vo.MerchantListVO;
import com.clou.userdemo.service.UserRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LENOVO
 */
@RestController
@RequestMapping("/user/shop")
@Slf4j
public class UserShopController {

    @Autowired
    ShopClient shopClient;
    @Autowired
    UserRecommendService userRecommendService;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    MerchantMapper merchantMapper;

    /**
     * 条件查询商品列表 - 分页查询
     * @param shopEsDTO
     * @return
     */
    @PostMapping("/list")
    @UserOperationLog(operateType = 1)
    public Result<PageVO<ShopEsVO>> getShopList(@RequestBody ShopEsDTO shopEsDTO,
                                                @RequestHeader(value = "id", required = false) Long userId) {
        log.info("查询商品{}",shopEsDTO);
        return Result.success(shopClient.searchShop(shopEsDTO));
    }

    /**
     * 用户商品推荐 - 基于用户行为日志的个性化推荐
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result<PageVO<ShopEsVO>>
     */
    @GetMapping("/recommend")
    public Result<PageVO<ShopEsVO>> getRecommendProducts(
            @RequestHeader(value = "id", required = false) Long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        log.info("获取推荐商品 userId={} pageNum={} pageSize={}", userId, pageNum, pageSize);
        return Result.success(userRecommendService.getRecommendProducts(userId, pageNum, pageSize));
    }

    /**
     * 查询商品详情(查看部分信息)
     * @param shopId
     * @return
     */
    @GetMapping("/details/{shopId}")
    public Result<ShopDetailsUserVO> byShopIdDetails(@PathVariable Long shopId) {
        log.info("查询商品详情{}",shopId);
        ShopDetailsUserVO shopDetailsUserVO = new ShopDetailsUserVO();
        BeanUtils.copyProperties(shopClient.byShopIdDetails(shopId), shopDetailsUserVO);
        return Result.success(shopDetailsUserVO);
    }

    /**
     * 查询指定商家的所有商品（分页）
     * @param merchantId 商家ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result<PageVO<ShopEsVO>>
     */
    @GetMapping("/merchant/{merchantId}")
    public Result<PageVO<ShopEsVO>> getShopListByMerchant(
            @PathVariable Long merchantId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {
        log.info("查询商家{}的商品列表 pageNum={} pageSize={}", merchantId, pageNum, pageSize);
        ShopEsDTO shopEsDTO = new ShopEsDTO();
        shopEsDTO.setMerchantId(merchantId);
        shopEsDTO.setPageNum(pageNum);
        return Result.success(shopClient.searchShop(shopEsDTO));
    }

    /**
     * 获取商品分类列表
     * @return Result<List<CategoryVO>>
     */
    @GetMapping("/categoryList")
    public Result<List<CategoryVO>> getCategoryList() {
        log.info("获取商品分类列表");
        return Result.success(categoryMapper.getCategoryList());
    }

    /**
     * 查询商家列表（分页查询，固定100条/页）
     * @param username 商家名称（可选，模糊查询）
     * @param pageNum 页码（默认1）
     * @return Result<PageVO<MerchantListVO>>
     */
    @GetMapping("/merchantList")
    public Result<PageVO> getMerchantList(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        log.info("查询商家列表 username={} pageNum={}", username, pageNum);

        int pageSize = 100;
        int offset = (pageNum - 1) * pageSize;

        List<MerchantPO> merchantPOList = merchantMapper.selectMerchantList(username, offset, pageSize);
        Long total = merchantMapper.countMerchantList(username);

        List<MerchantListVO> merchantListVOList = new ArrayList<>();
        for (MerchantPO po : merchantPOList) {
            MerchantListVO vo = new MerchantListVO();
            BeanUtils.copyProperties(po, vo);
            merchantListVOList.add(vo);
        }

        PageVO pageVO = new PageVO();
        pageVO.setTotal((long) total.intValue());
        pageVO.setData(merchantListVOList);

        return Result.success(pageVO);
    }

}
