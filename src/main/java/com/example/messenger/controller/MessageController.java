package com.example.messenger.controller;

import com.example.messenger.documens.Message;
import com.example.messenger.exceptions.InvalidUserStatus;
import com.example.messenger.payload.request.MessageRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor
@Tag(
        name="Контроллер сообщений" +
                "(Message Controller)",
        description="В этом контроллере описаны методы обработки и отправки сообщений " +
                "(This controller describes the messaging management and sending methods)")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Operation(
            summary = "Отправка сообщения" +
                    "(Send Message)",
            description = "Позволяет отправлять сообщения между пользователями" +
                    "(Allows users to send messages to each other)"
    )
    @MessageMapping("/send-message")
    public void sendMessage(MessageRequest messageRequest) {
        try {
            messageService
                    .sendMessage(
                            messageRequest.getSenderUsername(),
                            messageRequest.getRecipientUsername(),
                            messageRequest.getText()
                    );
        } catch (UsernameNotFoundException e) {
            messagingTemplate.convertAndSend("/topic/errors",
                    new MessageResponse("Invalid usernames"));
        } catch (AccessDeniedException e) {
            messagingTemplate.convertAndSend("/topic/errors",
                    new MessageResponse("You don't have permission to send message of user with username:"
                    +messageRequest.getRecipientUsername()));
        }
        catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/errors",
                    new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }



    @Operation(
            summary = "История чата" +
                    "(Chat History)",
            description = "Позволяет получать историю сообщений между пользователями" +
                    "(Allows users to get the message history between them)"
    )
    @GetMapping("/chat-history")
    public ResponseEntity<Object> getChatHistory(
            @RequestParam("senderUsername") String senderUsername,
            @RequestParam("recipientUsername") String recipientUsername) {
        try {
            List<Message> chatHistory = messageService.getChatHistory(senderUsername, recipientUsername);
            return  ResponseEntity.ok(chatHistory);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Incorrect usernames"));
        } catch (InvalidUserStatus e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+recipientUsername+" deleted account"));
        }
    }

}
