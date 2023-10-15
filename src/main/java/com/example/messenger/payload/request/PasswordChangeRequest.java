package com.example.messenger.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Schema(description = "Модель запроса для изменения пароля" +
        " (Password change request model)")
public class PasswordChangeRequest {

    @Schema(
            description = "Имя пользователя" +
                    " (Username)",
            example = "User123")
    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @Schema(
            description = "Старый пароль" +
                    " (Old password)",
            example = "password123",
            minLength = 6)
    private String password;

    @Schema(
            description = "Новый пароль" +
                    " (New password)",
            example = "newPassword123",
            minLength = 6)
    @Size(min = 6,message = "Password must contain at least 6 characters")
    @NotEmpty(message = "Password can't be empty")
    private String newPassword;
}