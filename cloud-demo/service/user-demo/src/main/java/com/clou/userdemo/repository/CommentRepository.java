package com.clou.userdemo.repository;

import com.clou.userdemo.POJO.po.CommentPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 评论数据访问层（MongoDB）
 */
@Repository
public interface CommentRepository extends MongoRepository<CommentPO, String> {

    /**
     * 根据商品ID分页查询主评论，按点赞数降序
     */
    Page<CommentPO> findByProductIdOrderByLikeCountDesc(String productId, Pageable pageable);

    /**
     * 根据ID查询主评论
     */
    Optional<CommentPO> findById(String id);

    /**
     * 删除主评论
     */
    void deleteById(String id);

    /**
     * 主评论点赞数+1
     */
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likeCount': 1 } }")
    void incrementMainCommentLike(String commentId);

    /**
     * 主评论点赞数-1
     */
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likeCount': -1 } }")
    void decrementMainCommentLike(String commentId);

    /**
     * 子评论点赞数+1
     */
    @Query("{ '_id': ?0, 'replyList.replyId': ?1 }")
    @Update("{ '$inc': { 'replyList.$.likeCount': 1 } }")
    void incrementReplyLike(String commentId, String replyId);

    /**
     * 子评论点赞数-1
     */
    @Query("{ '_id': ?0, 'replyList.replyId': ?1 }")
    @Update("{ '$inc': { 'replyList.$.likeCount': -1 } }")
    void decrementReplyLike(String commentId, String replyId);
}
