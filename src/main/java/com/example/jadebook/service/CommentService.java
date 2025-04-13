package com.example.jadebook.service;

import com.example.jadebook.dto.CommentDTO;
import com.example.jadebook.entity.Comments;

import java.util.List;

public interface CommentService {
    Comments createComment(Long postId, Long userId, String content);
    List<CommentDTO> getCommentsByPostId(Long postId); // 新增方法
}