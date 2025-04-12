package com.example.jadebook.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private UserData data;
    private String token;
}