package com.example.messenger.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Модель запроса для выхода из системы" +
        "(Logout request model)")
public class LogOutRequest {

    @Schema(
            description = "Имя пользователя" +
                    " (Username)",
            example = "User123")
    @NotEmpty(message = "Username cannot be empty")
    private String username;

}
