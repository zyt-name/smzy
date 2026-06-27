package com.clou.userdemo.POJO.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发表子评论（回复）请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    private String commentId;

    private String content;

    private Long toUserId;

    private String toUsername;
}
