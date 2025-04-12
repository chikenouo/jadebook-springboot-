package com.example.jadebook.service.impl;

import com.example.jadebook.entity.Comments;
import com.example.jadebook.entity.Comments;
import com.example.jadebook.entity.Post;
import com.example.jadebook.mapper.CommentMapper;
import com.example.jadebook.mapper.PostMapper;
import com.example.jadebook.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public Comments createComment(Long postId, Long userId, String content) {
        // 檢查發文是否存在
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("發文不存在");
        }

        // 創建留言
        Comments comment = new Comments();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        commentMapper.insert(comment);

        // 顯式查詢以回填 createdAt
        Comments savedComment = commentMapper.selectById(comment.getCommentId());
        System.out.println("CommentServiceImpl: Created comment with createdAt: " + savedComment.getCreatedAt());
        return savedComment;
    }
}