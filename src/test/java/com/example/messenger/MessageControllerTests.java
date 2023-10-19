package com.example.messenger;


import com.example.messenger.controller.MessageController;
import com.example.messenger.documens.Message;
import com.example.messenger.exceptions.InvalidUserStatus;
import com.example.messenger.payload.request.MessageRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testUser")
class MessageControllerTests {

    @MockBean
    private MessageService messageService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;
    @BeforeEach
    public void setup() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.blockingQueue = new ArrayBlockingQueue<>(1);
    }


    @Test
    public void testGetChatHistory() throws Exception, InvalidUserStatus {
        String senderUsername = "sender";
        String recipientUsername = "recipient";
        List<Message> messages = new ArrayList<>();
        Message message = new Message();
        message.setText("Hello!");
        messages.add(message);

        when(messageService.getChatHistory(senderUsername, recipientUsername)).thenReturn(messages);

        mockMvc.perform(get("/chat-history")
                        .param("senderUsername", senderUsername)
                        .param("recipientUsername", recipientUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("Hello!"));

        verify(messageService, times(1)).getChatHistory(anyString(), anyString());
    }

    @Test
    public void testGetChatHistoryIncorrectUsernames() throws Exception, InvalidUserStatus {
        String senderUsername = "sender";
        String recipientUsername = "invalid";

        when(messageService.getChatHistory(senderUsername, recipientUsername))
                .thenThrow(new UsernameNotFoundException("Incorrect usernames"));

        mockMvc.perform(get("/chat-history")
                        .param("senderUsername", "sender")
                        .param("recipientUsername", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incorrect usernames"));

        verify(messageService, times(1)).getChatHistory(anyString(), anyString());
    }
    @AfterEach
    public void tearDown() {
        if (stompClient != null) {
            stompClient.stop();
        }
    }

    @Test
    public void testSendMessage() throws Exception {
        String SEND_HELLO_MESSAGE_ENDPOINT = "/app/send-message";

        String senderUsername = "sender";
        String recipientUsername = "recipient";
        String text="Hello!";
        String webSocketUrl = "ws://localhost:8080";
        StompSession session = stompClient
                .connect(webSocketUrl + "/message-websocket", new StompSessionHandlerAdapter() {
                })
                .get(10000, SECONDS);

        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setSenderUsername(senderUsername);
        messageRequest.setRecipientUsername(recipientUsername);
        messageRequest.setText(text);
        session.send(SEND_HELLO_MESSAGE_ENDPOINT, messageRequest);

    }


    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            blockingQueue.offer((String) payload);
        }
    }


}
