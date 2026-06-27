package com.clou.userdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 评论分页返回数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPageVO {

    private Long total;

    private List<CommentVO> list;
}
