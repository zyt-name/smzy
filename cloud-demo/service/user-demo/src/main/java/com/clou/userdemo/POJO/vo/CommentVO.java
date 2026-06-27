package com.clou.userdemo.POJO.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 主评论返回数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {

    private String id;

    private String productId;

    private Long userId;

    private String username;

    private String content;

    private Integer likeCount;

    private LocalDateTime createTime;

    private List<ReplyVO> replyList;

    private Long replyTotal;
}
