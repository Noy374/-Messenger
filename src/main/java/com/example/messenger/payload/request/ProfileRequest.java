package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueUsername;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "Модель запроса для профиля пользователя" +
        "(Profile request model)")
@Data
public class ProfileRequest {

    @Schema(
            description = "Имя" +
                    " (Name)",
            example = "Oleg"
    )
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Schema(
            description = "Имя пользователя" +
                    " (Username)",
            example = "Oleggg"
    )
    @NotEmpty(message = "Username cannot be empty")
    @UniqueUsername
    private String username;

    @Schema(
            description = "Фамилия" +
                    " (Surname)",
            example = "Olegov"
    )
    @NotEmpty(message = "Surname cannot be empty")
    private String surname;
}