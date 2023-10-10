package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueUsername;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @Size(min = 6,message = "Password must contain at least 6 characters")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

}