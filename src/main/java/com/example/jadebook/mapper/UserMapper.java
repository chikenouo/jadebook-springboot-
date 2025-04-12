package com.example.jadebook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jadebook.entity.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<Users> {
    Users findByPhoneNumber(String phoneNumber);
    Users findByEmail(String email); // 保留此方法，因為註冊時需要驗證電子郵件唯一性
}