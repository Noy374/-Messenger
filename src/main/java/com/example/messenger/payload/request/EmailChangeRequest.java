package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueEmail;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailChangeRequest {
    @NotEmpty(message = "Email cannot be empty")
    @UniqueEmail
    private String email;
}
