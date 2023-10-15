package com.example.messenger.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "Модель запроса для отправки сообщения (Message sending request model)")
public class MessageRequest {

    @Schema(
            description = "Имя пользователя-отправителя (Sender's Username)",
            example = "Sender123")
    @NotEmpty(message = "Sender's Username cannot be empty")
    private String senderUsername;

    @Schema(
            description = "Имя пользователя-получателя (Recipient's Username)",
            example = "Recipient123")
    @NotEmpty(message = "Recipient's Username cannot be empty")
    private String recipientUsername;

    @Schema(
            description = "Текст сообщения (Message Text)",
            example = "Hello, how are you?")
    @NotEmpty(message = "Message Text cannot be empty")
    private String text;

}
