package com.example.jadebook.service;

import com.example.jadebook.entity.Users;

public interface UserService {
    Users registerUser(String userName, String phoneNumber, String email, String password, String coverImage, String biography);
    String loginUser(String phoneNumber, String password);
    Users findByPhoneNumber(String phoneNumber);
    // 新增方法
    Users findByUserId(Long userId);
}