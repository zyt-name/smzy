package com.clou.userdemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发表主评论请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private String productId;

    private String content;
}
