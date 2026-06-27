package com.clou.admindemo.service;

import com.clou.apidemo.POJO.po.ShopPO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AdminShopService {

    List<ShopPO> getAllShop(@RequestParam int pageNum,
                            @RequestParam int pageSize);

}
