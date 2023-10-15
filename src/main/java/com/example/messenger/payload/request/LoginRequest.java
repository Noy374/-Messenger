package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueUsername;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Модель запроса для логина" +
        "(Login request model)")
public class LoginRequest {

    @Schema(
            description = "Имя пользователя" +
                    " (Username)",
            example = "User123")
    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @Schema(
            description = "Пароль" +
                    " (Password)",
            example = "password123",
            minLength = 6)
    @Size(min = 6,message = "Password must contain at least 6 characters")
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}