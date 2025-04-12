package com.example.jadebook.controller;

import com.example.jadebook.dto.UserData;
import com.example.jadebook.entity.Users;
import com.example.jadebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/me")
    public ResponseEntity<UserData> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new RuntimeException("未授權，請先登入");
        }

        String userId = ((UserDetails) principal).getUsername();
        Users user = userService.findByUserId(Long.parseLong(userId));
        if (user == null) {
            throw new RuntimeException("用戶不存在");
        }

        UserData userData = new UserData();
        userData.setUserName(user.getUserName());
        userData.setPhoneNumber(user.getPhoneNumber());
        userData.setEmail(user.getEmail());
        userData.setCoverImage(user.getCoverImage() != null ? user.getCoverImage() : "");
        userData.setBiography(user.getBiography() != null ? user.getBiography() : "");

        return ResponseEntity.ok(userData);
    }
}