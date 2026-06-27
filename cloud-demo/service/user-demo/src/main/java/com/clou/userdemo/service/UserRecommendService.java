package com.clou.userdemo.service;

import com.clou.apidemo.POJO.vo.PageVO;
import com.clou.apidemo.POJO.vo.ShopEsVO;

public interface UserRecommendService {

    // 用户商品推荐 - 基于用户行为日志的个性化推荐
    PageVO<ShopEsVO> getRecommendProducts(Long userId, Integer pageNum, Integer pageSize);

    // 刷新所有用户的推荐缓存
    void refreshAllRecommendCache();

}
