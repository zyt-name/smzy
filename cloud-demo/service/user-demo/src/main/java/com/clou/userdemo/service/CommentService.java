package com.clou.userdemo.service;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.CommentDTO;
import com.clou.userdemo.POJO.dto.ReplyDTO;
import com.clou.userdemo.POJO.vo.CommentPageVO;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 发表主评论
     */
    Result<Void> addComment(CommentDTO commentDTO, Long userId, String username);

    /**
     * 分页查询商品评论（主评论分页，每个主评论下子评论也分页）
     */
    Result<CommentPageVO> getComments(String productId, Integer page, Integer size, Integer replyPage, Integer replySize);

    /**
     * 发表子评论（回复）
     */
    Result<Void> addReply(ReplyDTO replyDTO, Long userId, String username);

    /**
     * 删除主评论
     */
    Result<Void> deleteComment(String commentId, Long userId);

    /**
     * 删除子评论
     */
    Result<Void> deleteReply(String commentId, String replyId, Long userId);

    /**
     * 点赞主评论
     */
    Result<Void> likeComment(String commentId);

    /**
     * 点赞子评论
     */
    Result<Void> likeReply(String commentId, String replyId);
}
