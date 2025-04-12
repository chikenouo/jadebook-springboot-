package com.example.jadebook.service;

import com.example.jadebook.entity.Comments;

public interface CommentService {
    // 新增留言
    Comments createComment(Long postId, Long userId, String content);
}