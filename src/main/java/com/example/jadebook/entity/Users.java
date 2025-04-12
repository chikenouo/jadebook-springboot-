package com.example.jadebook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class Users {
    @TableId(value = "user_id",type = IdType.ASSIGN_ID)
    private Long userId; // 修改為 Long 型別匹配資料庫 BIGINT
    // 使用者電話號碼作為 userId
    private String phoneNumber; // 電話號碼，作為帳號，唯一約束

    private String userName;
    private String email;
    private String password; // 儲存加鹽雜湊後的密碼
    private String coverImage; // 照片，非必要欄位
    private String biography; // 自我介紹
}