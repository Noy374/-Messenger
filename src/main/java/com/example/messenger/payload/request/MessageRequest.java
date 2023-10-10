package com.example.messenger.payload.request;

import lombok.Data;

@Data
public class MessageRequest {
    private String  senderUsername;
    private String recipientUsername;
    private String text;
}
