package com.example.jadebook.service;

import com.example.jadebook.dto.PostDTO;
import com.example.jadebook.dto.PostResponseDTO;
import com.example.jadebook.entity.Post;

import java.util.List;

public interface PostService {
    Post createPost(Long userId, String content, String image);
    List<PostResponseDTO> getAllPosts(); // 移除 includeComments 參數
    Post getPostById(Long postId);
    Post updatePost(Long postId, Long userId, String content, String image);
    void deletePost(Long postId, Long userId);
}