package com.clou.admindemo.controller;

import com.clou.admindemo.POJO.DTO.GoodsTagCategoryDTO;
import com.clou.admindemo.POJO.DTO.GoodsTagCategoryQueryDTO;
import com.clou.admindemo.POJO.po.GoodsTagCategoryPO;
import com.clou.admindemo.POJO.vo.GoodsTagCategoryVO;
import com.clou.admindemo.POJO.vo.LabelStatsVO;
import com.clou.admindemo.mapper.GoodsTagCategoryMapper;
import com.clou.admindemo.service.GoodsTagCategoryService;
import com.clou.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/Label")
@Slf4j
public class AdminLabelController {

    @Autowired
    private GoodsTagCategoryService goodsTagCategoryService;

    @Autowired
    private GoodsTagCategoryMapper goodsTagCategoryMapper;

    @PostMapping("/add")
    public Result<String> add(@RequestBody GoodsTagCategoryDTO goodsTagCategoryDTO,
                              @RequestHeader("username") String username) {
        log.info("添加标签/分类: {}, 创建人: {}", goodsTagCategoryDTO, username);
        goodsTagCategoryService.add(goodsTagCategoryDTO, username);
        return Result.success("添加成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除标签/分类, id: {}", id);
        goodsTagCategoryService.delete(id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/deleteBatch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        log.info("批量删除标签/分类, ids: {}", ids);
        goodsTagCategoryService.deleteBatch(ids);
        return Result.success("批量删除成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody GoodsTagCategoryPO goodsTagCategoryPO) {
        log.info("更新标签/分类: {}", goodsTagCategoryPO);
        goodsTagCategoryService.update(goodsTagCategoryPO);
        return Result.success("更新成功");
    }

    @GetMapping("/category/list")
    public Result<List<GoodsTagCategoryPO>> listCategory(GoodsTagCategoryQueryDTO queryDTO) {
        log.info("查询商品分类列表, 条件: {}", queryDTO);
        queryDTO.setTagType(1);
        List<GoodsTagCategoryPO> list = goodsTagCategoryMapper.selectByCondition(queryDTO);
        return Result.success(list);
    }

    @GetMapping("/tag/list")
    public Result<List<GoodsTagCategoryPO>> listTag(GoodsTagCategoryQueryDTO queryDTO) {
        log.info("查询商品标签列表, 条件: {}", queryDTO);
        queryDTO.setTagType(2);
        List<GoodsTagCategoryPO> list = goodsTagCategoryMapper.selectByCondition(queryDTO);
        return Result.success(list);
    }

    @GetMapping("/tag/lazyList")
    public Result<List<GoodsTagCategoryVO>> lazyListTag(
            @RequestParam(required = false) Long parentTagId,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdTimeMonth) {
        log.info("懒加载查询商品标签, parentTagId: {}, level: {}, tagName: {}, status: {}, createdTimeMonth: {}", 
                parentTagId, level, tagName, status, createdTimeMonth);
        
        GoodsTagCategoryQueryDTO queryDTO = new GoodsTagCategoryQueryDTO();
        queryDTO.setTagType(2);
        queryDTO.setParentTagId(parentTagId);
        queryDTO.setLevel(level);
        queryDTO.setTagName(tagName);
        queryDTO.setStatus(status);
        queryDTO.setCreatedTimeMonth(createdTimeMonth);
        
        List<GoodsTagCategoryPO> list = goodsTagCategoryMapper.selectByCondition(queryDTO);
        
        List<GoodsTagCategoryVO> voList = list.stream().map(po -> {
            GoodsTagCategoryVO vo = new GoodsTagCategoryVO();
            vo.setId(po.getId());
            vo.setTagName(po.getTagName());
            vo.setTagType(po.getTagType());
            vo.setParentTagId(po.getParentTagId());
            vo.setStatus(po.getStatus());
            vo.setRemark(po.getRemark());
            vo.setCreatedBy(po.getCreatedBy());
            vo.setCreatedTime(po.getCreatedTime());
            vo.setLevel(po.getLevel());
            
            if (po.getLevel() != null && po.getLevel() < 3) {
                vo.setHasChildren(true);
            } else {
                GoodsTagCategoryQueryDTO childQuery = new GoodsTagCategoryQueryDTO();
                childQuery.setTagType(2);
                childQuery.setParentTagId(po.getId());
                List<GoodsTagCategoryPO> children = goodsTagCategoryMapper.selectByCondition(childQuery);
                vo.setHasChildren(!children.isEmpty());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(voList);
    }

    @GetMapping("/stats")
    public Result<LabelStatsVO> getStats() {
        log.info("查询标签统计数据");
        
        GoodsTagCategoryQueryDTO categoryQuery = new GoodsTagCategoryQueryDTO();
        categoryQuery.setTagType(1);
        List<GoodsTagCategoryPO> categoryList = goodsTagCategoryMapper.selectByCondition(categoryQuery);
        
        GoodsTagCategoryQueryDTO tagQuery = new GoodsTagCategoryQueryDTO();
        tagQuery.setTagType(2);
        List<GoodsTagCategoryPO> tagList = goodsTagCategoryMapper.selectByCondition(tagQuery);
        
        int categoryCount = categoryList.size();
        int categoryEnabledCount = (int) categoryList.stream().filter(item -> item.getStatus() == 1).count();
        
        int tagCount = tagList.size();
        int level1Count = (int) tagList.stream().filter(item -> item.getLevel() != null && item.getLevel() == 1).count();
        int level2Count = (int) tagList.stream().filter(item -> item.getLevel() != null && item.getLevel() == 2).count();
        int level3Count = (int) tagList.stream().filter(item -> item.getLevel() != null && item.getLevel() == 3).count();
        int tagEnabledCount = (int) tagList.stream().filter(item -> item.getStatus() == 1).count();
        
        LabelStatsVO statsVO = new LabelStatsVO(
            categoryCount,
            categoryEnabledCount,
            tagCount,
            level1Count,
            level2Count,
            level3Count,
            tagEnabledCount
        );
        
        return Result.success(statsVO);
    }
}
