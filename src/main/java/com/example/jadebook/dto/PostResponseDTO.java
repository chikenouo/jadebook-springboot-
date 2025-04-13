package com.example.jadebook.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponseDTO {
    private Long postId;
    private Long userId;
    private String userName;
    private String content;
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    private int commentsCount; // 新增 commentsCount 字段
}