package com.example.jadebook.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {
    private Long postId;
    private Long userId;
    private String userName;
    private String content;
    private String image;
    private LocalDateTime createdAt;
}