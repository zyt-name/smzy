package com.clou.userdemo.service.impl;

import com.clou.common.exception.BusinessException;
import com.clou.userdemo.POJO.po.MerchantAddressPO;
import com.clou.userdemo.POJO.po.MerchantPO;
import com.clou.userdemo.POJO.po.UserFavoriteMerchantPO;
import com.clou.userdemo.POJO.vo.FavoriteMerchantVO;
import com.clou.userdemo.POJO.vo.MerchantInfoVO;
import com.clou.userdemo.POJO.vo.PageVO;
import com.clou.userdemo.mapper.MerchantAddressMapper;
import com.clou.userdemo.mapper.MerchantMapper;
import com.clou.userdemo.mapper.UserFavoriteMerchantMapper;
import com.clou.userdemo.service.UserFavoriteMerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserFavoriteMerchantServiceImpl implements UserFavoriteMerchantService {

    @Autowired
    UserFavoriteMerchantMapper userFavoriteMerchantMapper;

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    MerchantAddressMapper merchantAddressMapper;

    @Override
    public void addFavoriteMerchant(Long userId, Long merchantId) {
        UserFavoriteMerchantPO existPO = userFavoriteMerchantMapper.selectByUserIdAndMerchantId(userId, merchantId);
        if (existPO != null) {
            throw new BusinessException(500, "该店铺已经收藏，请勿重复操作！");
        }
        UserFavoriteMerchantPO userFavoriteMerchantPO = new UserFavoriteMerchantPO();
        userFavoriteMerchantPO.setUserId(userId);
        userFavoriteMerchantPO.setMerchantId(merchantId);
        userFavoriteMerchantMapper.insert(userFavoriteMerchantPO);
        log.info("用户{}收藏商家{}成功", userId, merchantId);
    }

    /**
     * 删除用户收藏商家
     * @param userId 用户ID
     * @param merchantId 商家ID
     */
    @Override
    public void deleteFavoriteMerchant(Long userId, Long merchantId) {
        userFavoriteMerchantMapper.deleteByUserIdAndMerchantId(userId, merchantId);
        log.info("用户{}取消收藏商家{}", userId, merchantId);
    }

    /**
     * 分页查询用户收藏商家列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return PageVO<FavoriteMerchantVO>
     */
    @Override
    public PageVO getFavoriteMerchantList(Long userId, int pageNum, int pageSize) {
        List<UserFavoriteMerchantPO> favoriteList = userFavoriteMerchantMapper.selectByUserId(userId);
        if (favoriteList == null || favoriteList.isEmpty()) {
            PageVO emptyPageVO = new PageVO();
            emptyPageVO.setCount(0);
            emptyPageVO.setData(Collections.emptyList());
            return emptyPageVO;
        }

        List<Integer> merchantIds = favoriteList.stream()
                .map(UserFavoriteMerchantPO::getMerchantId)
                .map(Long::intValue)
                .collect(Collectors.toList());

        List<MerchantPO> merchantList = merchantMapper.selectByIds(merchantIds);
        Map<Integer, MerchantPO> merchantMap = merchantList.stream()
                .collect(Collectors.toMap(MerchantPO::getId, m -> m));

        List<MerchantAddressPO> addressList = merchantAddressMapper.selectByMerchantIds(merchantIds);
        Map<Integer, List<MerchantAddressPO>> addressMap = addressList.stream()
                .collect(Collectors.groupingBy(MerchantAddressPO::getMerchantId));

        List<FavoriteMerchantVO> result = new ArrayList<>();
        for (UserFavoriteMerchantPO favorite : favoriteList) {
            Integer merchantIdInt = favorite.getMerchantId().intValue();
            MerchantPO merchant = merchantMap.get(merchantIdInt);
            if (merchant == null) continue;

            FavoriteMerchantVO vo = new FavoriteMerchantVO();
            vo.setId(favorite.getId());
            vo.setMerchantId(favorite.getMerchantId());
            vo.setMerchantName(merchant.getUsername());
            vo.setPhone(merchant.getMerchantNo());
            vo.setStatus(merchant.getStatus());

            List<MerchantAddressPO> addresses = addressMap.getOrDefault(merchantIdInt, Collections.emptyList());
            List<FavoriteMerchantVO.MerchantAddressVO> addressVOList = addresses.stream()
                    .map(addr -> {
                        FavoriteMerchantVO.MerchantAddressVO addressVO = new FavoriteMerchantVO.MerchantAddressVO();
                        addressVO.setProvince(addr.getProvince());
                        addressVO.setCity(addr.getCity());
                        addressVO.setDistrict(addr.getDistrict());
                        addressVO.setDetailAddress(addr.getDetailAddress());
                        return addressVO;
                    })
                    .collect(Collectors.toList());
            vo.setAddressList(addressVOList);

            result.add(vo);
        }

        int total = result.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<FavoriteMerchantVO> pageData = start >= total ? Collections.emptyList() : result.subList(start, end);

        PageVO pageVO = new PageVO();
        pageVO.setCount(total);
        pageVO.setData(pageData);
        return pageVO;
    }

    /**
     * 查询当前用户是否已收藏指定商家
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @return boolean 是否已收藏
     */
    @Override
    public boolean isFavorite(Long userId, Long merchantId) {
        UserFavoriteMerchantPO existPO = userFavoriteMerchantMapper.selectByUserIdAndMerchantId(userId, merchantId);
        return existPO != null;
    }

    /**
     * 查询指定商家信息
     * @param merchantId 商家ID
     * @return MerchantInfoVO 商家信息
     */
    @Override
    public MerchantInfoVO getMerchantInfo(Long merchantId) {
        MerchantPO merchant = merchantMapper.selectById(merchantId.intValue());
        if (merchant == null) {
            throw new BusinessException(500, "商家不存在");
        }

        MerchantInfoVO vo = new MerchantInfoVO();
        vo.setMerchantId(merchantId);
        vo.setMerchantName(merchant.getUsername());
        vo.setPhone(merchant.getMerchantNo());
        vo.setStatus(merchant.getStatus());

        List<MerchantAddressPO> addresses = merchantAddressMapper.selectByMerchantId(merchantId.intValue());
        List<MerchantInfoVO.AddressInfo> addressList = addresses.stream()
                .map(addr -> {
                    MerchantInfoVO.AddressInfo addressInfo = new MerchantInfoVO.AddressInfo();
                    addressInfo.setProvince(addr.getProvince());
                    addressInfo.setCity(addr.getCity());
                    addressInfo.setDistrict(addr.getDistrict());
                    addressInfo.setDetailAddress(addr.getDetailAddress());
                    return addressInfo;
                })
                .collect(Collectors.toList());
        vo.setAddressList(addressList);

        return vo;
    }

}