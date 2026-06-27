package com.clou.apidemo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "merchant-demo")
public interface MerchantClient {

    /**
     * 管理员更新商户状态
     * @param merchantId
     * @param status
     * @return
     */
    @PutMapping("/merchant-demo/merchant/admin/updateStatus")
    boolean updateMerchantStatus(@RequestParam("merchantId") Long merchantId,
                                 @RequestParam("status") int status);

}
