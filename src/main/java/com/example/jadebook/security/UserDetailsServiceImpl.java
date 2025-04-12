package com.example.jadebook.security;

import com.example.jadebook.entity.Users;
import com.example.jadebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        Users user = userService.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new UsernameNotFoundException("用戶不存在");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUserId().toString(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}