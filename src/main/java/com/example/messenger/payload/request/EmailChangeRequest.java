package com.example.messenger.payload.request;

import com.example.messenger.anatations.UniqueEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailChangeRequest {

    @Schema(
            description = "Новый адрес электронной почты" +
                    "(New email)",
            example = "example@gmail.com")
    @NotEmpty(message = "Email cannot be empty")
    @UniqueEmail
    private String email;
}