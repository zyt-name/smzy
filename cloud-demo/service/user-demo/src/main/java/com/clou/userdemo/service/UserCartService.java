package com.clou.userdemo.service;

import com.clou.userdemo.POJO.dto.UserCartDTO;
import com.clou.userdemo.POJO.vo.UserCartVO;

import java.util.List;

public interface UserCartService {

    // 添加购物车
    void addCart(UserCartDTO userCartDTO, Long userId);

    // 查看购物车
    List<UserCartVO> getCart(Long userId);

    // 修改购物车中的商品数量(添加或者减少)
    void updateCartCount(UserCartDTO userCartDTO, Long userId);

    // 清空购物车
    void deleteCart(Long userId);

    // 删除购物车中单个商品
    void deleteCartItem(UserCartDTO userCartDTO, Long userId);

}
