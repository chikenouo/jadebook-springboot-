package com.example.jadebook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.jadebook.dto.CommentDTO;
import com.example.jadebook.entity.Comments;
import com.example.jadebook.entity.Post;
import com.example.jadebook.entity.Users;
import com.example.jadebook.mapper.CommentMapper;
import com.example.jadebook.mapper.PostMapper;
import com.example.jadebook.mapper.UserMapper;
import com.example.jadebook.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Comments createComment(Long postId, Long userId, String content) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("發文不存在");
        }

        Comments comment = new Comments();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        Comments savedComment = commentMapper.selectById(comment.getCommentId());
        System.out.println("CommentServiceImpl: Created comment with createdAt: " + savedComment.getCreatedAt());
        return savedComment;
    }

    @Override
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        // 檢查貼文是否存在
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("發文不存在");
        }

        // 查詢留言
        List<Comments> comments = commentMapper.selectList(
                new QueryWrapper<Comments>().eq("post_id", postId)
        );

        return comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setCommentId(comment.getCommentId());
            commentDTO.setPostId(comment.getPostId());
            commentDTO.setUserId(comment.getUserId());
            commentDTO.setContent(comment.getContent());
            commentDTO.setCreatedAt(comment.getCreatedAt());

            // 查詢留言者的用戶名
            Users user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                commentDTO.setUserName(user.getUserName());
            }
            return commentDTO;
        }).collect(Collectors.toList());
    }
}