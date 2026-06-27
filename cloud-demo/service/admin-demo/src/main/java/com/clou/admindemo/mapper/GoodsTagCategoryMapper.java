package com.clou.admindemo.mapper;

import com.clou.admindemo.POJO.DTO.GoodsTagCategoryQueryDTO;
import com.clou.admindemo.POJO.po.GoodsTagCategoryPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsTagCategoryMapper {

    int insert(GoodsTagCategoryPO goodsTagCategoryPO);

    int deleteByIds(List<Long> ids);

    int update(GoodsTagCategoryPO goodsTagCategoryPO);

    List<Long> selectIdsByParentId(Long parentId);

    List<GoodsTagCategoryPO> selectByCondition(GoodsTagCategoryQueryDTO queryDTO);
}
