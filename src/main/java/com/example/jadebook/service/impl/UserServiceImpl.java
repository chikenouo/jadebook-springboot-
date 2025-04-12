package com.example.jadebook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.jadebook.entity.Users;
import com.example.jadebook.mapper.UserMapper;
import com.example.jadebook.security.JwtUtil;
import com.example.jadebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Users registerUser(String userName, String phoneNumber, String email, String password, String coverImage, String biography) {
        if (userMapper.selectOne(new QueryWrapper<Users>().eq("phone_number", phoneNumber)) != null) {
            throw new RuntimeException("電話號碼已存在");
        }
        if (userMapper.selectOne(new QueryWrapper<Users>().eq("email", email)) != null) {
            throw new RuntimeException("信箱已存在");
        }
        if (!phoneNumber.matches("\\d{10}")) {
            throw new RuntimeException("電話號碼必須為 10 位數字");
        }
        if (password.length() < 8) {
            throw new RuntimeException("密碼必須至少 8 位數");
        }
        String encodedPassword = passwordEncoder.encode(password);
        Users user = new Users();
        user.setUserName(userName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setCoverImage(coverImage);
        user.setBiography(biography);
        userMapper.insert(user);
        return user;
    }

    @Override
    public String loginUser(String phoneNumber, String password) {
        Users user = userMapper.selectOne(new QueryWrapper<Users>().eq("phone_number", phoneNumber));
        if (user == null) {
            throw new RuntimeException("無效的電話號碼或密碼");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("無效的電話號碼或密碼");
        }
        String userId = String.valueOf(user.getUserId());
        System.out.println("Generating JWT with userId: " + userId);
        return jwtUtil.generateToken(userId);
    }

    @Override
    public Users findByPhoneNumber(String phoneNumber) {
        return userMapper.selectOne(new QueryWrapper<Users>().eq("phone_number", phoneNumber));
    }

    @Override
    public Users findByUserId(Long userId) {
        return userMapper.selectById(userId);
    }
}