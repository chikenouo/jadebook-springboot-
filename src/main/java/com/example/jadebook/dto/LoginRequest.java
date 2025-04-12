package com.example.jadebook.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber; // 僅使用電話號碼作為登入帳號
    private String password;
}