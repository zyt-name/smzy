package com.clou.admindemo.service;

import com.clou.admindemo.POJO.DTO.GoodsTagCategoryDTO;
import com.clou.admindemo.POJO.po.GoodsTagCategoryPO;

import java.util.List;

public interface GoodsTagCategoryService {

    void add(GoodsTagCategoryDTO goodsTagCategoryDTO, String createdBy);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    void update(GoodsTagCategoryPO goodsTagCategoryPO);
}
