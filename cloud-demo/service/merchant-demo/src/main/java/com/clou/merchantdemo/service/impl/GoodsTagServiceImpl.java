package com.clou.merchantdemo.service.impl;

import com.clou.merchantdemo.POJO.dto.GoodsTagQueryDTO;
import com.clou.merchantdemo.POJO.vo.GoodsTagVO;
import com.clou.merchantdemo.mapper.GoodsTagMapper;
import com.clou.merchantdemo.service.GoodsTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GoodsTagServiceImpl implements GoodsTagService {

    @Autowired
    GoodsTagMapper goodsTagMapper;

    @Override
    public List<GoodsTagVO> getTagNameList(GoodsTagQueryDTO queryDTO) {
        log.info("查询商品标签名称列表，条件：{}", queryDTO);
        return goodsTagMapper.getTagNameList(queryDTO);
    }

}
