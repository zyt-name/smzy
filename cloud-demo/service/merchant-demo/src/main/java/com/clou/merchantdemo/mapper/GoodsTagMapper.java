package com.clou.merchantdemo.mapper;

import com.clou.merchantdemo.POJO.dto.GoodsTagQueryDTO;
import com.clou.merchantdemo.POJO.vo.GoodsTagVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsTagMapper {

    List<GoodsTagVO> getTagNameList(GoodsTagQueryDTO queryDTO);

}
