package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueEmail;
import com.example.messenger.anatations.UniqueUsername;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {


    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotEmpty(message = "Username cannot be empty")
    @UniqueUsername
    private String username;
    @NotEmpty(message = "Surname cannot be empty")
    private String surname;
    @Size(min = 6,message = "Password must contain at least 6 characters")
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    @NotEmpty(message = "Email cannot be empty")
    @UniqueEmail
    private String email;

}