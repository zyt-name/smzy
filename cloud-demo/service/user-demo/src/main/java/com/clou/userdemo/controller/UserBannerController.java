package com.clou.userdemo.controller;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.po.BannerPO;
import com.clou.userdemo.POJO.vo.BannerVO;
import com.clou.userdemo.repository.BannerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/banner")
@Slf4j
public class UserBannerController {

    @Autowired
    private BannerRepository bannerRepository;

    @GetMapping("/list")
    public Result<List<BannerVO>> getBannerList() {
        log.info("获取 Banner 列表");
        List<BannerPO> banners = bannerRepository.findAllByOrderBySortOrderAscCreateTimeDesc();
        List<BannerVO> bannerVOList = banners.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(bannerVOList);
    }

    @PostMapping("/click/{bannerId}")
    public Result<Void> incrementClick(@PathVariable String bannerId) {
        log.info("增加 Banner 点击率: bannerId={}", bannerId);
        bannerRepository.incrementClickCount(bannerId);
        return Result.success();
    }

    private BannerVO convertToVO(BannerPO bannerPO) {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setId(bannerPO.getId());
        bannerVO.setTitle(bannerPO.getTitle());
        bannerVO.setImagePath(bannerPO.getImagePath());
        bannerVO.setGoodsId(bannerPO.getGoodsId());
        bannerVO.setClickCount(bannerPO.getClickCount());
        bannerVO.setSortOrder(bannerPO.getSortOrder());
        bannerVO.setCreateTime(bannerPO.getCreateTime());
        bannerVO.setUpdateTime(bannerPO.getUpdateTime());
        return bannerVO;
    }
}
