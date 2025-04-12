package com.example.jadebook.service;

import com.example.jadebook.dto.PostDTO;
import com.example.jadebook.entity.Post;

import java.util.List;

public interface PostService {

    // 新增發文
    Post createPost(Long userId, String content, String image);

    // 列出所有發文（包含用戶資訊）
    List<PostDTO> getAllPosts();

    // 根據 postId 查找發文
    Post getPostById(Long postId);

    // 編輯發文
    Post updatePost(Long postId, Long userId, String content, String image);

    // 刪除發文
    void deletePost(Long postId, Long userId);
}