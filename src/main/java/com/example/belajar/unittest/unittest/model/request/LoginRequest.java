package com.example.belajar.unittest.unittest.model.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class LoginRequest {
    @NonNull
    private String username;

    @NonNull
    private String password;

}
