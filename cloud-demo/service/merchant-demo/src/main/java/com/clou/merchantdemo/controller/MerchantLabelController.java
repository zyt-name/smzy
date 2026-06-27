package com.clou.merchantdemo.controller;

import com.clou.common.result.Result;
import com.clou.merchantdemo.POJO.dto.GoodsTagQueryDTO;
import com.clou.merchantdemo.POJO.vo.GoodsTagVO;
import com.clou.merchantdemo.service.GoodsTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchantLabel")
@Slf4j
public class MerchantLabelController {

    @Autowired
    GoodsTagService goodsTagService;

    @PostMapping("/getTagNameList")
    public Result<List<GoodsTagVO>> getTagNameList(@RequestBody GoodsTagQueryDTO queryDTO) {
        log.info("查询商品标签名称列表：{}", queryDTO);
        List<GoodsTagVO> tagNameList = goodsTagService.getTagNameList(queryDTO);
        return Result.success(tagNameList);
    }

}
