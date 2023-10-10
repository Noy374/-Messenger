package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueUsername;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LogOutRequest {

    @NotEmpty(message = "Username cannot be empty")
    private String username;

}