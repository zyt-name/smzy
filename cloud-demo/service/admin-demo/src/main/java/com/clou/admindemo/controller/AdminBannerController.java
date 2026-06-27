package com.clou.admindemo.controller;

import com.clou.admindemo.POJO.DTO.BannerDTO;
import com.clou.admindemo.POJO.DTO.BannerUpdateDTO;
import com.clou.admindemo.POJO.vo.BannerClickStatsVO;
import com.clou.admindemo.POJO.vo.BannerVO;
import com.clou.admindemo.service.BannerService;
import com.clou.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/banner")
public class AdminBannerController {

    @Autowired
    private BannerService bannerService;

    @PostMapping
    public Result<Void> addBanner(@RequestBody BannerDTO bannerDTO) {
        return bannerService.addBanner(bannerDTO);
    }

    @GetMapping("/stats")
    public Result<List<BannerClickStatsVO>> getClickStats() {
        return bannerService.getClickStats();
    }

    @PutMapping
    public Result<Void> updateBanner(@RequestBody BannerUpdateDTO bannerUpdateDTO) {
        return bannerService.updateBanner(bannerUpdateDTO);
    }

    @GetMapping
    public Result<List<BannerVO>> getAllBanners() {
        return bannerService.getAllBanners();
    }

    @DeleteMapping("/{bannerId}")
    public Result<Void> deleteBanner(@PathVariable String bannerId) {
        return bannerService.deleteBanner(bannerId);
    }
}
