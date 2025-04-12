package com.example.jadebook.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phoneNumber;
    private String userName;
    private String email;
    private String password;
    private String coverImage;
    private String biography;
}
