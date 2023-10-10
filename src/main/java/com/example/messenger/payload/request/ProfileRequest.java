package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueUsername;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProfileRequest {
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotEmpty(message = "Username cannot be empty")
    @UniqueUsername
    private String username;
    @NotEmpty(message = "Surname cannot be empty")
    private String surname;
}
