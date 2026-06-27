package com.clou.userdemo.mapper;

import com.clou.userdemo.POJO.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT id, tag_name as tagName FROM goods_tag_category WHERE tag_type = 1 AND status = 1")
    List<CategoryVO> getCategoryList();
}
