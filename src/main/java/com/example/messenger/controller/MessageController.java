package com.example.messenger.controller;

import com.example.messenger.documens.Message;
import com.example.messenger.payload.request.MessageRequest;
import com.example.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private  final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/hello")
    @SendTo("/topic/messages")
    public void sendMessage(MessageRequest messageRequest) {
        messageService
                .sendMessage(
                        messageRequest.getSenderUsername(),
                        messageRequest.getRecipientUsername(),
                        messageRequest.getText()
                );
    }

    @MessageMapping("/chat")
    public void getChatHistory(MessageRequest messageRequest) {
        List<Message> chatHistory = messageService.getChatHistory(
                messageRequest.getSenderUsername(),
                messageRequest.getRecipientUsername());

        messagingTemplate.convertAndSend("/topic/messages", chatHistory);
    }
}