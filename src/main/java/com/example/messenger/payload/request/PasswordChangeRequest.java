package com.example.messenger.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class PasswordChangeRequest {

    @NotEmpty(message = "Username cannot be empty")
    private String username;
    private String Password;
    @Size(min = 6,message = "Password must contain at least 6 characters")
    @NotEmpty(message = "Password cannot be empty")
    private String newPassword;
}
