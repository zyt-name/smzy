package com.clou.merchantdemo.service;

import com.clou.merchantdemo.POJO.dto.GoodsTagQueryDTO;
import com.clou.merchantdemo.POJO.vo.GoodsTagVO;

import java.util.List;

public interface GoodsTagService {

    List<GoodsTagVO> getTagNameList(GoodsTagQueryDTO queryDTO);

}
