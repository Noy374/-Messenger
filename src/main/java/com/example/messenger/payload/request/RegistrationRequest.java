package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueEmail;
import com.example.messenger.anatations.UniqueUsername;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Schema(description = "Модель запроса для регистрации" +
        "(Registration request model)")
@Data
public class RegistrationRequest {

    @Schema(
            description = "Имя пользователя" +
                    "(Name)",
            example = "Ivan")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Schema(
            description = "Уникальное имя пользователя" +
                    "(Unique Username)",
            example = "User123")
    @NotEmpty(message = "Username cannot be empty")
    @UniqueUsername
    private String username;

    @Schema(
            description = "Фамилия пользователя" +
                    "(Surname)",
            example = "Ivanov")
    @NotEmpty(message = "Surname cannot be empty")
    private String surname;

    @Schema(
            description = "Пароль" +
                    "(Password)",
            example = "password123",
            minLength = 6)
    @Size(min = 6, message = "Password must contain at least 6 characters")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @Schema(
            description = "Электронная почта" +
                    "(Email)",
            example = "user@example.com")
    @NotEmpty(message = "Email cannot be empty")
    @UniqueEmail
    @Email
    private String email;
}