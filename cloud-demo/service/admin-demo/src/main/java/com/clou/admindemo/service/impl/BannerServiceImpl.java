package com.clou.admindemo.service.impl;

import com.clou.admindemo.POJO.DTO.BannerDTO;
import com.clou.admindemo.POJO.DTO.BannerUpdateDTO;
import com.clou.admindemo.POJO.po.BannerPO;
import com.clou.admindemo.POJO.vo.BannerClickStatsVO;
import com.clou.admindemo.POJO.vo.BannerVO;
import com.clou.admindemo.repository.BannerRepository;
import com.clou.admindemo.mapper.StatisticsMapper;
import com.clou.admindemo.service.BannerService;
import com.clou.common.exception.BusinessException;
import com.clou.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public Result<Void> addBanner(BannerDTO bannerDTO) {
        log.info("添加 Banner: title={}, goodsId={}", bannerDTO.getTitle(), bannerDTO.getGoodsId());

        int count = statisticsMapper.existsByGoodsId(bannerDTO.getGoodsId());
        if (count == 0) {
            log.warn("商品不存在: goodsId={}", bannerDTO.getGoodsId());
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "该商品不存在，请核对数据！");
        }

        BannerPO bannerPO = new BannerPO();
        bannerPO.setId(UUID.randomUUID().toString().replace("-", ""));
        bannerPO.setTitle(bannerDTO.getTitle());
        bannerPO.setImagePath(bannerDTO.getImagePath());
        bannerPO.setGoodsId(bannerDTO.getGoodsId());
        bannerPO.setClickCount(0);
        bannerPO.setSortOrder(bannerDTO.getSortOrder() != null ? bannerDTO.getSortOrder() : 0);
        bannerPO.setCreateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        bannerPO.setUpdateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        bannerRepository.save(bannerPO);
        log.info("Banner 添加成功: bannerId={}", bannerPO.getId());

        return Result.success();
    }

    @Override
    public Result<List<BannerClickStatsVO>> getClickStats() {
        log.info("获取所有 Banner 点击率统计");

        List<BannerPO> banners = bannerRepository.findAll();

        long totalClicks = banners.stream()
                .mapToLong(BannerPO::getClickCount)
                .sum();

        List<BannerClickStatsVO> statsList = new ArrayList<>();
        for (BannerPO banner : banners) {
            BannerClickStatsVO statsVO = new BannerClickStatsVO();
            statsVO.setBannerId(banner.getId());
            statsVO.setTitle(banner.getTitle());
            statsVO.setClickCount(banner.getClickCount());

            if (totalClicks > 0) {
                statsVO.setClickRate((double) banner.getClickCount() / totalClicks * 100);
            } else {
                statsVO.setClickRate(0.0);
            }

            statsList.add(statsVO);
        }

        statsList.sort((a, b) -> b.getClickCount().compareTo(a.getClickCount()));

        return Result.success(statsList);
    }

    @Override
    public Result<Void> updateBanner(BannerUpdateDTO bannerUpdateDTO) {
        log.info("更新 Banner: bannerId={}", bannerUpdateDTO.getId());

        Optional<BannerPO> optionalBanner = bannerRepository.findById(bannerUpdateDTO.getId());
        if (!optionalBanner.isPresent()) {
            log.warn("Banner 不存在: bannerId={}", bannerUpdateDTO.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND.value(), "Banner 不存在");
        }

        BannerPO bannerPO = optionalBanner.get();

        if (bannerUpdateDTO.getTitle() != null) {
            bannerPO.setTitle(bannerUpdateDTO.getTitle());
        }
        if (bannerUpdateDTO.getImagePath() != null) {
            bannerPO.setImagePath(bannerUpdateDTO.getImagePath());
        }
        if (bannerUpdateDTO.getGoodsId() != null) {
            int count = statisticsMapper.existsByGoodsId(bannerUpdateDTO.getGoodsId());
            if (count == 0) {
                log.warn("商品不存在: goodsId={}", bannerUpdateDTO.getGoodsId());
                throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "该商品不存在，请核对数据！");
            }
            bannerPO.setGoodsId(bannerUpdateDTO.getGoodsId());
        }
        if (bannerUpdateDTO.getSortOrder() != null) {
            bannerPO.setSortOrder(bannerUpdateDTO.getSortOrder());
        }

        bannerPO.setUpdateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        bannerRepository.save(bannerPO);
        log.info("Banner 更新成功: bannerId={}", bannerPO.getId());

        return Result.success();
    }

    @Override
    public Result<List<BannerVO>> getAllBanners() {
        log.info("获取所有 Banner");

        List<BannerPO> banners = bannerRepository.findAllByOrderBySortOrderAscCreateTimeDesc();

        List<BannerVO> bannerVOList = banners.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(bannerVOList);
    }

    @Override
    public Result<Void> deleteBanner(String bannerId) {
        log.info("删除 Banner: bannerId={}", bannerId);

        if (!bannerRepository.existsById(bannerId)) {
            log.warn("Banner 不存在: bannerId={}", bannerId);
            throw new BusinessException(HttpStatus.NOT_FOUND.value(), "Banner 不存在");
        }

        bannerRepository.deleteById(bannerId);
        log.info("Banner 删除成功: bannerId={}", bannerId);

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
