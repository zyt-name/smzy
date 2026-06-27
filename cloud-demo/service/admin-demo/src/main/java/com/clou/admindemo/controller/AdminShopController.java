package com.clou.admindemo.controller;

import com.clou.admindemo.POJO.po.UserBanLogPO;
import com.clou.admindemo.mapper.UserBanLogMapper;
import com.clou.apidemo.POJO.dto.AdminShopSearchDTO;
import com.clou.apidemo.POJO.po.ShopPO;
import com.clou.apidemo.POJO.vo.PageVO;
import com.clou.apidemo.POJO.vo.ShopDetailsVO;
import com.clou.apidemo.POJO.vo.ShopEsVO;
import com.clou.apidemo.client.ShopClient;
import com.clou.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/shop")
@Slf4j
public class AdminShopController {

    @Autowired
    ShopClient shopClient;

    @Autowired
    UserBanLogMapper userBanLogMapper;

    /**
     * 管理员更新商品admin_status状态（强制下架/恢复正常）并记录日志
     * @param shopId 商品ID
     * @param adminStatus 管理员状态（0-恢复正常，1-强制下架）
     * @param reason 操作原因
     * @param adminId 管理员ID（从请求头获取）
     * @return
     */
    @PutMapping("/updateAdminStatus")
    public Result updateShopAdminStatus(@RequestParam Long shopId,
                                        @RequestParam Integer adminStatus,
                                        @RequestParam String reason,
                                        @RequestHeader("id") Long adminId) {
        log.info("管理员{}更新商品{}的admin_status为{}，原因：{}", adminId, shopId, adminStatus, reason);

        // 调用商品服务更新admin_status
        shopClient.updateShopAdminStatus(shopId, adminStatus);

        // 记录操作日志到user_ban_log表
        UserBanLogPO logPO = new UserBanLogPO();
        logPO.setBanId(shopId);
        logPO.setBanType(3); // 3=商品
        logPO.setOperatorId(adminId);
        logPO.setOperateType(adminStatus == 1 ? 1 : 2); // 1=封禁(强制下架), 2=解封(恢复正常)
        logPO.setReason(reason);
        logPO.setCreateTime(LocalDateTime.now());
        userBanLogMapper.insert(logPO);

        return Result.success();
    }

    /**
     * 分页查询商品（支持动态条件：商品名称模糊查询、商家ID、商品分类）
     * @param name 商品名称（模糊查询，可选）
     * @param merchantId 商家ID（精确查询，可选）
     * @param category 商品分类（精确查询，可选）
     * @param pageNum 页码，默认1
     * @param pageSize 每页条数，默认12
     * @return
     */
    @GetMapping("/search")
    public Result<PageVO<ShopEsVO>> searchShops(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize) {
        log.info("管理员分页查询商品，名称：{}，商家ID：{}，分类：{}，页码：{}，每页：{}",
                name, merchantId, category, pageNum, pageSize);

        AdminShopSearchDTO searchDTO = new AdminShopSearchDTO();
        searchDTO.setName(name);
        searchDTO.setMerchantId(merchantId);
        searchDTO.setCategory(category);
        searchDTO.setPageNum(pageNum);
        searchDTO.setPageSize(pageSize);

        return Result.success(shopClient.adminSearchShop(searchDTO));
    }

    /**
     * 根据商户ID查询商品
     * @param merchantId
     * @return
     */
    @GetMapping("/byMerchantId/{merchantId}")
    public Result<List<ShopPO>> byMerchantId(@PathVariable("merchantId") Long merchantId) {
        log.info("查询商户{}的商品",merchantId);
        return Result.success(shopClient.byMerchantId(merchantId));
    }

    /**
     * 根据商品ID查询商品详情(全量数据)
     * @param shopId
     * @return
     */
    @GetMapping("/details/{shopId}")
    public Result<ShopDetailsVO> byShopIdDetails(@PathVariable("shopId") Long shopId) {
        log.info("查询商品{}的详情",shopId);
        return Result.success(shopClient.byShopIdDetails(shopId));
    }

//    /**
//     * 全部商品列表
//     * @param pageNum
//     * @param pageSize
//     * @return
//     */
//    @GetMapping("/list")
//    public Result<List<ShopPO>> shopList(@RequestParam int pageNum,
//                                         @RequestParam int pageSize){
//        log.info("查询商品列表");
//        return Result.success(shopClient.shopList(pageNum,pageSize));
//    }

}
