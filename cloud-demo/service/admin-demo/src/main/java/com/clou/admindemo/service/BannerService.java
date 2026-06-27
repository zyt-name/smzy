package com.clou.admindemo.service;

import com.clou.admindemo.POJO.DTO.BannerDTO;
import com.clou.admindemo.POJO.DTO.BannerUpdateDTO;
import com.clou.admindemo.POJO.vo.BannerClickStatsVO;
import com.clou.admindemo.POJO.vo.BannerVO;
import com.clou.common.result.Result;

import java.util.List;

public interface BannerService {

    Result<Void> addBanner(BannerDTO bannerDTO);

    Result<List<BannerClickStatsVO>> getClickStats();

    Result<Void> updateBanner(BannerUpdateDTO bannerUpdateDTO);

    Result<List<BannerVO>> getAllBanners();

    Result<Void> deleteBanner(String bannerId);
}
