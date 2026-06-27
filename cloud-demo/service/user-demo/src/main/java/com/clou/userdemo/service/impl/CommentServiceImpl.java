package com.clou.userdemo.service.impl;

import com.clou.common.result.Result;
import com.clou.userdemo.POJO.dto.CommentDTO;
import com.clou.userdemo.POJO.dto.ReplyDTO;
import com.clou.userdemo.POJO.po.CommentPO;
import com.clou.userdemo.POJO.vo.CommentPageVO;
import com.clou.userdemo.POJO.vo.CommentVO;
import com.clou.userdemo.POJO.vo.ReplyVO;
import com.clou.userdemo.repository.CommentRepository;
import com.clou.userdemo.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Result<Void> addComment(CommentDTO commentDTO, Long userId, String username) {
        log.info("发表主评论: userId={}, productId={}", userId, commentDTO.getProductId());

        CommentPO commentPO = new CommentPO();
        commentPO.setId(UUID.randomUUID().toString().replace("-", ""));
        commentPO.setProductId(commentDTO.getProductId());
        commentPO.setUserId(userId);
        commentPO.setUsername(username);
        commentPO.setContent(commentDTO.getContent());
        commentPO.setLikeCount(0);
        commentPO.setCreateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        commentPO.setReplyList(new ArrayList<>());

        commentRepository.save(commentPO);
        log.info("主评论发表成功: commentId={}", commentPO.getId());

        return Result.success();
    }

    @Override
    public Result<CommentPageVO> getComments(String productId, Integer page, Integer size, Integer replyPage, Integer replySize) {
        log.info("查询商品评论: productId={}, page={}, size={}", productId, page, size);

        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : size;
        replyPage = replyPage == null || replyPage < 1 ? 1 : replyPage;
        replySize = replySize == null || replySize < 1 ? 10 : replySize;

        // 主评论分页查询，按点赞数降序
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CommentPO> commentPage = commentRepository.findByProductIdOrderByLikeCountDesc(productId, pageable);

        List<CommentVO> commentVOList = new ArrayList<>();

        for (CommentPO commentPO : commentPage.getContent()) {
            CommentVO commentVO = convertToCommentVO(commentPO, replyPage, replySize);
            commentVOList.add(commentVO);
        }

        CommentPageVO result = new CommentPageVO();
        result.setTotal(commentPage.getTotalElements());
        result.setList(commentVOList);

        return Result.success(result);
    }

    /**
     * 将 PO 转换为 VO，并对子评论进行分页
     */
    private CommentVO convertToCommentVO(CommentPO commentPO, Integer replyPage, Integer replySize) {
        CommentVO commentVO = new CommentVO();
        commentVO.setId(commentPO.getId());
        commentVO.setProductId(commentPO.getProductId());
        commentVO.setUserId(commentPO.getUserId());
        commentVO.setUsername(commentPO.getUsername());
        commentVO.setContent(commentPO.getContent());
        commentVO.setLikeCount(commentPO.getLikeCount());
        commentVO.setCreateTime(commentPO.getCreateTime());

        List<CommentPO.ReplyPO> replyList = commentPO.getReplyList();
        if (replyList == null) {
            replyList = new ArrayList<>();
        }

        // 子评论按点赞数降序排序
        replyList.sort((a, b) -> b.getLikeCount().compareTo(a.getLikeCount()));

        // 子评论分页
        int totalReplies = replyList.size();
        int fromIndex = (replyPage - 1) * replySize;
        int toIndex = Math.min(fromIndex + replySize, totalReplies);

        List<ReplyVO> replyVOList = new ArrayList<>();
        if (fromIndex < totalReplies) {
            List<CommentPO.ReplyPO> pagedReplies = replyList.subList(fromIndex, toIndex);
            for (CommentPO.ReplyPO replyPO : pagedReplies) {
                replyVOList.add(convertToReplyVO(replyPO));
            }
        }

        commentVO.setReplyList(replyVOList);
        commentVO.setReplyTotal((long) totalReplies);

        return commentVO;
    }

    private ReplyVO convertToReplyVO(CommentPO.ReplyPO replyPO) {
        ReplyVO replyVO = new ReplyVO();
        replyVO.setReplyId(replyPO.getReplyId());
        replyVO.setUserId(replyPO.getUserId());
        replyVO.setUsername(replyPO.getUsername());
        replyVO.setContent(replyPO.getContent());
        replyVO.setLikeCount(replyPO.getLikeCount());
        replyVO.setCreateTime(replyPO.getCreateTime());
        replyVO.setToUserId(replyPO.getToUserId());
        replyVO.setToUsername(replyPO.getToUsername());
        return replyVO;
    }

    @Override
    public Result<Void> addReply(ReplyDTO replyDTO, Long userId, String username) {
        log.info("发表子评论: commentId={}, userId={}", replyDTO.getCommentId(), userId);

        Optional<CommentPO> optionalComment = commentRepository.findById(replyDTO.getCommentId());
        if (!optionalComment.isPresent()) {
            log.warn("主评论不存在: commentId={}", replyDTO.getCommentId());
            return Result.error("主评论不存在");
        }

        CommentPO.ReplyPO replyPO = new CommentPO.ReplyPO();
        replyPO.setReplyId(UUID.randomUUID().toString().replace("-", ""));
        replyPO.setUserId(userId);
        replyPO.setUsername(username);
        replyPO.setContent(replyDTO.getContent());
        replyPO.setLikeCount(0);
        replyPO.setCreateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        replyPO.setToUserId(replyDTO.getToUserId());
        replyPO.setToUsername(replyDTO.getToUsername());

        // 使用 MongoTemplate 将子评论推送到 replyList 数组中
        Query query = new Query(Criteria.where("_id").is(replyDTO.getCommentId()));
        Update update = new Update().push("replyList", replyPO);
        mongoTemplate.updateFirst(query, update, CommentPO.class);

        log.info("子评论发表成功: replyId={}", replyPO.getReplyId());
        return Result.success();
    }

    @Override
    public Result<Void> deleteComment(String commentId, Long userId) {
        log.info("删除主评论: commentId={}, userId={}", commentId, userId);

        Optional<CommentPO> optionalComment = commentRepository.findById(commentId);
        if (!optionalComment.isPresent()) {
            log.warn("主评论不存在: commentId={}", commentId);
            return Result.error("主评论不存在");
        }

        CommentPO commentPO = optionalComment.get();
        if (!commentPO.getUserId().equals(userId)) {
            log.warn("无权删除: commentUserId={}, requestUserId={}", commentPO.getUserId(), userId);
            return Result.error("无权删除该评论");
        }

        commentRepository.deleteById(commentId);
        log.info("主评论删除成功: commentId={}", commentId);
        return Result.success();
    }

    @Override
    public Result<Void> deleteReply(String commentId, String replyId, Long userId) {
        log.info("删除子评论: commentId={}, replyId={}, userId={}", commentId, replyId, userId);

        Optional<CommentPO> optionalComment = commentRepository.findById(commentId);
        if (!optionalComment.isPresent()) {
            log.warn("主评论不存在: commentId={}", commentId);
            return Result.error("主评论不存在");
        }

        CommentPO commentPO = optionalComment.get();
        List<CommentPO.ReplyPO> replyList = commentPO.getReplyList();

        if (replyList == null || replyList.isEmpty()) {
            return Result.error("子评论不存在");
        }

        // 查找子评论并验证权限
        CommentPO.ReplyPO targetReply = null;
        for (CommentPO.ReplyPO reply : replyList) {
            if (reply.getReplyId().equals(replyId)) {
                targetReply = reply;
                break;
            }
        }

        if (targetReply == null) {
            log.warn("子评论不存在: replyId={}", replyId);
            return Result.error("子评论不存在");
        }

        if (!targetReply.getUserId().equals(userId)) {
            log.warn("无权删除: replyUserId={}, requestUserId={}", targetReply.getUserId(), userId);
            return Result.error("无权删除该回复");
        }

        // 使用 MongoTemplate 从 replyList 数组中删除指定子评论
        Query query = new Query(Criteria.where("_id").is(commentId));
        Update update = new Update().pull("replyList", new org.springframework.data.mongodb.core.query.Query(
                Criteria.where("replyId").is(replyId)).getQueryObject());
        mongoTemplate.updateFirst(query, update, CommentPO.class);

        log.info("子评论删除成功: replyId={}", replyId);
        return Result.success();
    }

    @Override
    public Result<Void> likeComment(String commentId) {
        log.info("点赞主评论: commentId={}", commentId);

        Optional<CommentPO> optionalComment = commentRepository.findById(commentId);
        if (!optionalComment.isPresent()) {
            log.warn("主评论不存在: commentId={}", commentId);
            return Result.error("主评论不存在");
        }

        commentRepository.incrementMainCommentLike(commentId);
        log.info("主评论点赞成功: commentId={}", commentId);
        return Result.success();
    }

    @Override
    public Result<Void> likeReply(String commentId, String replyId) {
        log.info("点赞子评论: commentId={}, replyId={}", commentId, replyId);

        Optional<CommentPO> optionalComment = commentRepository.findById(commentId);
        if (!optionalComment.isPresent()) {
            log.warn("主评论不存在: commentId={}", commentId);
            return Result.error("主评论不存在");
        }

        CommentPO commentPO = optionalComment.get();
        List<CommentPO.ReplyPO> replyList = commentPO.getReplyList();

        if (replyList == null || replyList.isEmpty()) {
            return Result.error("子评论不存在");
        }

        boolean replyExists = replyList.stream().anyMatch(r -> r.getReplyId().equals(replyId));
        if (!replyExists) {
            log.warn("子评论不存在: replyId={}", replyId);
            return Result.error("子评论不存在");
        }

        commentRepository.incrementReplyLike(commentId, replyId);
        log.info("子评论点赞成功: replyId={}", replyId);
        return Result.success();
    }
}
