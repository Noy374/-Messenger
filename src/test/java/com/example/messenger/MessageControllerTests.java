package com.example.messenger;


import com.example.messenger.documens.Message;
import com.example.messenger.payload.request.MessageRequest;
import com.example.messenger.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class MessageControllerTests {

    private static final String SEND_HELLO_MESSAGE_ENDPOINT = "/app/hello";
    private static final String SUBSCRIBE_GREETING_ENDPOINT = "/topic/greetings";

    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;

    @Autowired
    private MessageService messageService;
    @BeforeEach
    public void setup() {
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.blockingQueue = new ArrayBlockingQueue<>(1);
    }

    @Test
    public void shouldReceiveGreetingFromServer() throws Exception {

        String webSocketUrl = "ws://localhost:8080";
        StompSession session = stompClient
                .connect(webSocketUrl + "/message-websocket", new StompSessionHandlerAdapter() {
                })
                .get(10000, SECONDS);
        // send a message
        MessageRequest messageRequest=new MessageRequest();
        messageRequest.setRecipientUsername("Noy123456");
        messageRequest.setSenderUsername("Noy3743");
        messageRequest.setText("barev");
        session.send(SEND_HELLO_MESSAGE_ENDPOINT, messageRequest);
        session.subscribe(SUBSCRIBE_GREETING_ENDPOINT, new DefaultStompFrameHandler());


    }

    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            blockingQueue.offer((String) payload);
            System.out.println("Received response: " + payload);
        }
    }
    @Test
    public void shouldReturnChatHistory() throws Exception {
        List<Message> mockHistory=messageService.getChatHistory("Noy3743", "Noy123456");
        MessageRequest messageRequest=new MessageRequest();
        messageRequest.setRecipientUsername("Noy123456");
        messageRequest.setSenderUsername("Noy3743");
        String SEND_CHAT_HISTORY_MESSAGE_ENDPOINT = "/app/chat";
        String RECEIVE_CHAT_HISTORY_ENDPOINT = "/topic/messages";

        // Connect to web socket
        String webSocketUrl = "ws://localhost:8080";
        StompSession session = stompClient
                .connect(webSocketUrl + "/message-websocket", new StompSessionHandlerAdapter() {})
                .get(10000, SECONDS);

        // Subscribe to messages
        CompletableFuture<List<Message>> completableFuture = new CompletableFuture<>();
        session.subscribe(RECEIVE_CHAT_HISTORY_ENDPOINT, new DefaultStompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return List.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete((List<Message>) payload);
            }
        });

        // Send Message Request
        session.send(SEND_CHAT_HISTORY_MESSAGE_ENDPOINT, messageRequest);

        // Assert the result
        List<Message> actualHistory = completableFuture.get(10000, SECONDS);
        System.out.println(mockHistory.toString());
        System.out.println(actualHistory.toString());
        //assertEquals(mockHistory, actualHistory);
    }
    @AfterEach
    public void tearDown() {
        if (stompClient != null) {
            stompClient.stop();
        }
    }
}
