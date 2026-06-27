package com.clou.userdemo.POJO.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论文档实体（MongoDB）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comments")
public class CommentPO {

    @Id
    private String id;

    private String productId;

    private Long userId;

    private String username;

    private String content;

    private Integer likeCount;

    private LocalDateTime createTime;

    private List<ReplyPO> replyList;

    /**
     * 子评论实体
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyPO {

        private String replyId;

        private Long userId;

        private String username;

        private String content;

        private Integer likeCount;

        private LocalDateTime createTime;

        private Long toUserId;

        private String toUsername;
    }
}
