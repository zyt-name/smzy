package com.clou.userdemo.service;

import com.clou.userdemo.POJO.vo.FavoriteMerchantVO;
import com.clou.userdemo.POJO.vo.MerchantInfoVO;
import com.clou.userdemo.POJO.vo.PageVO;

public interface UserFavoriteMerchantService {

    void addFavoriteMerchant(Long userId, Long merchantId);

    void deleteFavoriteMerchant(Long userId, Long merchantId);

    PageVO getFavoriteMerchantList(Long userId, int pageNum, int pageSize);

    boolean isFavorite(Long userId, Long merchantId);

    MerchantInfoVO getMerchantInfo(Long merchantId);

}