package com.clou.userdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 子评论（回复）返回数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyVO {

    private String replyId;

    private Long userId;

    private String username;

    private String content;

    private Integer likeCount;

    private LocalDateTime createTime;

    private Long toUserId;

    private String toUsername;
}
