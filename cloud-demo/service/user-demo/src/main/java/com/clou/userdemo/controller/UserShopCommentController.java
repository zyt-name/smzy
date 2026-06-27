package com.clou.userdemo.controller;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.CommentDTO;
import com.clou.userdemo.POJO.dto.ReplyDTO;
import com.clou.userdemo.POJO.vo.CommentPageVO;
import com.clou.userdemo.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 商品评论控制器
 */
@RestController
@RequestMapping("/user/comment")
@Slf4j
public class UserShopCommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 发表主评论
     * @param commentDTO 评论内容
     * @param id 用户ID（从请求头获取）
     * @param username 用户名（从请求头获取）
     * @return
     */
    @PostMapping("/add")
    public Result<Void> addComment(@RequestBody CommentDTO commentDTO,
                                   @RequestHeader("id") Long id,
                                   @RequestHeader("username") String username) {
        // username 由 Gateway 从 token 解析并传入（已URL编码，需要解码）
        String decodedUsername = URLDecoder.decode(username, StandardCharsets.UTF_8);
        log.info("发表主评论: userId={}, username={}, productId={}", id, decodedUsername, commentDTO.getProductId());
        return commentService.addComment(commentDTO, id, decodedUsername);
    }

    /**
     * 分页查询商品评论
     * @param productId 商品ID
     * @param page 主评论页码（默认1）
     * @param size 主评论每页条数（默认10）
     * @param replyPage 子评论页码（默认1）
     * @param replySize 子评论每页条数（默认10）
     * @return
     */
    @GetMapping("/list")
    public Result<CommentPageVO> getComments(@RequestParam("productId") String productId,
                                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                                             @RequestParam(value = "replyPage", defaultValue = "1") Integer replyPage,
                                             @RequestParam(value = "replySize", defaultValue = "10") Integer replySize) {
        log.info("查询商品评论: productId={}, page={}, size={}", productId, page, size);
        return commentService.getComments(productId, page, size, replyPage, replySize);
    }

    /**
     * 发表子评论（回复）
     * @param replyDTO 回复内容
     * @param id 用户ID（从请求头获取）
     * @param username 用户名（从请求头获取）
     * @return
     */
    @PostMapping("/reply/add")
    public Result<Void> addReply(@RequestBody ReplyDTO replyDTO,
                                 @RequestHeader("id") Long id,
                                 @RequestHeader("username") String username) {
        // username 由 Gateway 从 token 解析并传入（已URL编码，需要解码）
        String decodedUsername = URLDecoder.decode(username, StandardCharsets.UTF_8);
        log.info("发表子评论: userId={}, username={}, commentId={}", id, decodedUsername, replyDTO.getCommentId());
        return commentService.addReply(replyDTO, id, decodedUsername);
    }

    /**
     * 删除主评论
     * @param commentId 主评论ID
     * @param id 用户ID（从请求头获取）
     * @return
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteComment(@RequestParam("commentId") String commentId,
                                      @RequestHeader("id") Long id) {
        log.info("删除主评论: commentId={}, userId={}", commentId, id);
        return commentService.deleteComment(commentId, id);
    }

    /**
     * 删除子评论
     * @param commentId 主评论ID
     * @param replyId 子评论ID
     * @param id 用户ID（从请求头获取）
     * @return
     */
    @DeleteMapping("/reply/delete")
    public Result<Void> deleteReply(@RequestParam("commentId") String commentId,
                                    @RequestParam("replyId") String replyId,
                                    @RequestHeader("id") Long id) {
        log.info("删除子评论: commentId={}, replyId={}, userId={}", commentId, replyId, id);
        return commentService.deleteReply(commentId, replyId, id);
    }

    /**
     * 点赞主评论
     * @param commentId 主评论ID
     * @return
     */
    @PostMapping("/like")
    public Result<Void> likeComment(@RequestParam("commentId") String commentId) {
        log.info("点赞主评论: commentId={}", commentId);
        return commentService.likeComment(commentId);
    }

    /**
     * 点赞子评论
     * @param commentId 主评论ID
     * @param replyId 子评论ID
     * @return
     */
    @PostMapping("/reply/like")
    public Result<Void> likeReply(@RequestParam("commentId") String commentId,
                                  @RequestParam("replyId") String replyId) {
        log.info("点赞子评论: commentId={}, replyId={}", commentId, replyId);
        return commentService.likeReply(commentId, replyId);
    }
}
