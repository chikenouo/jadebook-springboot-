package com.example.jadebook.controller;

import com.example.jadebook.dto.LoginResponse;
import com.example.jadebook.dto.UserData;
import com.example.jadebook.entity.Users;
import com.example.jadebook.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    // 註冊
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> userRequest) {
        String userName = userRequest.get("userName");
        String phoneNumber = userRequest.get("phoneNumber");
        String email = userRequest.get("email");
        String password = userRequest.get("password");
        String coverImage = userRequest.get("coverImage");
        String biography = userRequest.get("biography");

        userService.registerUser(userName, phoneNumber, email, password, coverImage, biography);

        Map<String, String> response = new HashMap<>();
        response.put("message", "註冊成功");
        return ResponseEntity.ok(response);
    }

    // 登入，使用 phoneNumber 進行登入
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        String phoneNumber = loginRequest.get("phoneNumber");
        String password = loginRequest.get("password");

        // 調用 UserService 進行登入並生成 token
        String token = userService.loginUser(phoneNumber, password);

        // 設置 HttpOnly Cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 小時
        response.addCookie(cookie);

        // 獲取用戶詳細資料
        Users user = userService.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new RuntimeException("用戶不存在");
        }

        // 構建響應
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("登入成功");

        UserData userData = new UserData();
        userData.setUserName(user.getUserName());
        userData.setPhoneNumber(user.getPhoneNumber());
        userData.setEmail(user.getEmail());
        userData.setCoverImage(user.getCoverImage() != null ? user.getCoverImage() : "");
        userData.setBiography(user.getBiography() != null ? user.getBiography() : "");

        loginResponse.setData(userData);
        // 可選擇是否在響應體中返回 token
        loginResponse.setToken(token);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 設置為 0 表示刪除 Cookie
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    // 異常處理
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "操作失敗");
        error.put("message", e.getMessage());
        return ResponseEntity.status(400).body(error);
    }
}