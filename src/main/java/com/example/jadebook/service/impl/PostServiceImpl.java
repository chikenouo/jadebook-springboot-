package com.example.jadebook.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.jadebook.dto.PostDTO;
import com.example.jadebook.entity.Post;
import com.example.jadebook.entity.Users;
import com.example.jadebook.mapper.PostMapper;
import com.example.jadebook.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Post createPost(Long userId, String content, String image) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImage(image);
        post.setCreatedAt(LocalDateTime.now());
        postMapper.insert(post);
        // 確認 createdAt
        System.out.println("PostServiceImpl: Created post with createdAt: " + post.getCreatedAt());
        return post;
    }

    @Override
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postMapper.selectList(new QueryWrapper<>());
        return posts.stream().map(post -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getPostId());
            postDTO.setUserId(post.getUserId());
            postDTO.setContent(post.getContent());
            postDTO.setImage(post.getImage());
            postDTO.setCreatedAt(post.getCreatedAt());

            // 查詢發文者的用戶名
            Users user = userMapper.selectById(post.getUserId());
            if (user != null) {
                postDTO.setUserName(user.getUserName());
            }
            return postDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Post getPostById(Long postId) {
        return postMapper.selectById(postId);
    }

    @Override
    public Post updatePost(Long postId, Long userId, String content, String image) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("發文不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("無權編輯此發文");
        }
        post.setContent(content);
        post.setImage(image);
        postMapper.updateById(post);
        return post;
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("發文不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("無權刪除此發文");
        }
        postMapper.deleteById(postId);
    }
}