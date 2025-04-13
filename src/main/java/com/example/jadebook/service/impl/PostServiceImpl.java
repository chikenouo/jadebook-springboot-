package com.example.jadebook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.jadebook.dto.PostResponseDTO;
import com.example.jadebook.entity.Comments;
import com.example.jadebook.entity.Post;
import com.example.jadebook.entity.Users;
import com.example.jadebook.mapper.CommentMapper;
import com.example.jadebook.mapper.PostMapper;
import com.example.jadebook.mapper.UserMapper;
import com.example.jadebook.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

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
        System.out.println("PostServiceImpl: Created post with createdAt: " + post.getCreatedAt());
        return post;
    }

    @Override
    public List<PostResponseDTO> getAllPosts() {
        // 查詢所有貼文
        List<Post> posts = postMapper.selectList(new QueryWrapper<>());

        // 一次性查詢所有貼文的留言數量
        List<Map<String, Object>> commentCounts = commentMapper.selectMaps(
                new QueryWrapper<Comments>()
                        .select("post_id", "COUNT(*) as count")
                        .groupBy("post_id")
        );

        // 將查詢結果轉換為 Map<Long, Integer>
        Map<Long, Integer> commentCountMap = commentCounts.stream()
                .collect(Collectors.toMap(
                        map -> ((Number) map.get("post_id")).longValue(), // post_id 轉為 Long
                        map -> ((Number) map.get("count")).intValue()     // count 轉為 int
                ));

        return posts.stream().map(post -> {
            PostResponseDTO postDTO = new PostResponseDTO();
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

            // 從 commentCountMap 中獲取留言數量，默認為 0
            postDTO.setCommentsCount(commentCountMap.getOrDefault(post.getPostId(), 0));
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