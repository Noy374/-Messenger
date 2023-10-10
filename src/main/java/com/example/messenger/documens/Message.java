package com.example.messenger.documens;


import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private Long senderId;
    private Long recipientId;
    private String text;
    private LocalDateTime timestamp;

}