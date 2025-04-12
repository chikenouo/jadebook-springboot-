package com.example.jadebook.controller;

import com.example.jadebook.dto.CommentDTO;
import com.example.jadebook.dto.PostDTO;
import com.example.jadebook.entity.Comments;
import com.example.jadebook.entity.Post;
import com.example.jadebook.entity.Users;
import com.example.jadebook.mapper.UserMapper;
import com.example.jadebook.service.CommentService;
import com.example.jadebook.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserMapper userMapper; // 注入 UserMapper 用於檢查用戶是否存在

    @Autowired
    private CommentService commentService; // 注入 CommentService

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody PostDTO postDTO) throws IOException {
        // 從 Spring Security 上下文中獲取 userId
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "未認證");
            error.put("message", "請先登錄");
            return ResponseEntity.status(401).body(error);
        }

        UserDetails userDetails = (UserDetails) principal;
        String userIdStr = userDetails.getUsername();
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "無效的用戶 ID");
            error.put("message", "用戶 ID 必須是數字");
            return ResponseEntity.status(400).body(error);
        }

        // 檢查用戶是否存在
        Users user = userMapper.selectById(userId);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "用戶不存在");
            error.put("message", "用戶 ID " + userId + " 不存在");
            return ResponseEntity.status(400).body(error);
        }

        // 處理圖片（Base64 編碼）
        String imagePath = null;
        if (postDTO.getImage() != null && !postDTO.getImage().isEmpty()) {
            // 移除 Base64 前綴（例如 "data:image/jpeg;base64,"）
            String base64Image = postDTO.getImage();
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String fileName = UUID.randomUUID().toString() + ".jpg"; // 假設圖片為 JPG 格式
            File dest = new File(uploadDir + File.separator + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(imageBytes);
            }
            imagePath = "/uploads/" + fileName;
        }

        // 調用服務層創建發文
        Post post = postService.createPost(userId, postDTO.getContent(), imagePath);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "發文成功");
        response.put("post", post);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable Long postId,
            @RequestBody PostDTO postDTO) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "未認證");
            error.put("message", "請先登錄");
            return ResponseEntity.status(401).body(error);
        }

        UserDetails userDetails = (UserDetails) principal;
        String userIdStr = userDetails.getUsername();
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "無效的用戶 ID");
            error.put("message", "用戶 ID 必須是數字");
            return ResponseEntity.status(400).body(error);
        }

        // 檢查用戶是否存在
        Users user = userMapper.selectById(userId);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "用戶不存在");
            error.put("message", "用戶 ID " + userId + " 不存在");
            return ResponseEntity.status(400).body(error);
        }

        String imagePath = null;
        if (postDTO.getImage() != null && !postDTO.getImage().isEmpty()) {
            String base64Image = postDTO.getImage();
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String fileName = UUID.randomUUID().toString() + ".jpg";
            File dest = new File(uploadDir + File.separator + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(imageBytes);
            }
            imagePath = "/uploads/" + fileName;
        }

        Post updatedPost = postService.updatePost(postId, userId, postDTO.getContent(), imagePath);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "編輯成功");
        response.put("post", updatedPost);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "未認證");
            error.put("message", "請先登錄");
            return ResponseEntity.status(401).body(error);
        }

        UserDetails userDetails = (UserDetails) principal;
        String userIdStr = userDetails.getUsername();
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "無效的用戶 ID");
            error.put("message", "用戶 ID 必須是數字");
            return ResponseEntity.status(400).body(error);
        }

        // 檢查用戶是否存在
        Users user = userMapper.selectById(userId);
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "用戶不存在");
            error.put("message", "用戶 ID " + userId + " 不存在");
            return ResponseEntity.status(400).body(error);
        }

        postService.deletePost(postId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "刪除成功");
        return ResponseEntity.ok(response);
    }

    // 新增留言端點
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> requestBody) {
        // 從 Spring Security 上下文中獲取 userId
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "未認證");
            error.put("message", "請先登錄");
            return ResponseEntity.status(401).body(error);
        }

        UserDetails userDetails = (UserDetails) principal;
        String userIdStr = userDetails.getUsername();
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "無效的用戶 ID");
            error.put("message", "用戶 ID 必須是數字");
            return ResponseEntity.status(400).body(error);
        }

        // 檢查用戶是否存在
        Users user = userMapper.selectById(userId);
        if (user == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "用戶不存在");
            error.put("message", "用戶 ID " + userId + " 不存在");
            return ResponseEntity.status(400).body(error);
        }

        // 獲取留言內容
        String content = requestBody.get("content");
        if (content == null || content.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "無效的留言內容");
            error.put("message", "留言內容不能為空");
            return ResponseEntity.status(400).body(error);
        }

        // 創建留言
        Comments comment = commentService.createComment(postId, userId, content);

        // 構建響應
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setCommentId(comment.getCommentId());
        commentDTO.setPostId(comment.getPostId());
        commentDTO.setUserId(comment.getUserId());
        commentDTO.setUserName(user.getUserName());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedAt(comment.getCreatedAt());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "留言成功");
        response.put("comment", commentDTO);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "伺服器錯誤");
        error.put("message", e.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}