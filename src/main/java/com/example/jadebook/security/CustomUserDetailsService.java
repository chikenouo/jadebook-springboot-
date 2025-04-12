package com.example.jadebook.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.jadebook.entity.Users;
import com.example.jadebook.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        Users user = userMapper.selectOne(new QueryWrapper<Users>().eq("phone_number", phoneNumber));
        if (user == null) {
            throw new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
        }
        return User.withUsername(String.valueOf(user.getUserId()))
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    public UserDetails loadUserByUserId(String userId) {
        Users user = userMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with userId: " + userId);
        }
        // 添加日誌，確認 userId
        System.out.println("CustomUserDetailsService: Setting username to: " + user.getUserId());
        return User.withUsername(String.valueOf(user.getUserId()))
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}