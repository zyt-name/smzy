package com.clou.admindemo.service.impl;

import com.clou.admindemo.POJO.DTO.GoodsTagCategoryDTO;
import com.clou.admindemo.POJO.po.GoodsTagCategoryPO;
import com.clou.admindemo.mapper.GoodsTagCategoryMapper;
import com.clou.admindemo.service.GoodsTagCategoryService;
import com.clou.common.constant.code;
import com.clou.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class GoodsTagCategoryServiceImpl implements GoodsTagCategoryService {

    @Autowired
    private GoodsTagCategoryMapper goodsTagCategoryMapper;

    @Override
    public void add(GoodsTagCategoryDTO goodsTagCategoryDTO, String createdBy) {
        // 构建 PO
        GoodsTagCategoryPO goodsTagCategoryPO = new GoodsTagCategoryPO();
        BeanUtils.copyProperties(goodsTagCategoryDTO, goodsTagCategoryPO);

        // 设置默认值
        goodsTagCategoryPO.setCreatedTime(LocalDateTime.now());
        goodsTagCategoryPO.setStatus(1);
        goodsTagCategoryPO.setCreatedBy(createdBy);

        int result = goodsTagCategoryMapper.insert(goodsTagCategoryPO);
        if (result <= 0) {
            throw new BusinessException(code.errorCode, "添加标签失败");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(code.errorCode, "标签ID不能为空");
        }
        // 收集所有待删除的ID（自身 + 所有后代）
        Set<Long> idsToDelete = new HashSet<>();
        idsToDelete.add(id);
        collectChildrenIds(id, idsToDelete);

        // 批量删除
        int result = goodsTagCategoryMapper.deleteByIds(new ArrayList<>(idsToDelete));
        if (result <= 0) {
            throw new BusinessException(code.errorCode, "删除标签失败");
        }
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(code.errorCode, "标签ID列表不能为空");
        }
        // 收集所有待删除的ID（包括子标签），使用Set去重
        Set<Long> idsToDelete = new HashSet<>();
        for (Long id : ids) {
            if (id != null) {
                idsToDelete.add(id);
                collectChildrenIds(id, idsToDelete);
            }
        }

        // 批量删除
        int result = goodsTagCategoryMapper.deleteByIds(new ArrayList<>(idsToDelete));
        if (result <= 0) {
            throw new BusinessException(code.errorCode, "批量删除标签失败");
        }
    }

    private void collectChildrenIds(Long parentId, Set<Long> ids) {
        List<Long> childrenIds = goodsTagCategoryMapper.selectIdsByParentId(parentId);
        for (Long childId : childrenIds) {
            ids.add(childId);
            // 递归收集子标签的ID
            collectChildrenIds(childId, ids);
        }
    }

    @Override
    public void update(GoodsTagCategoryPO goodsTagCategoryPO) {
        if (goodsTagCategoryPO.getId() == null) {
            throw new BusinessException(code.errorCode, "标签ID不能为空");
        }
        int result = goodsTagCategoryMapper.update(goodsTagCategoryPO);
        if (result <= 0) {
            throw new BusinessException(code.errorCode, "更新标签失败");
        }
    }
}
