package com.example.messenger.AuthControllerTests;


import com.example.messenger.payload.request.LoginRequest;
import com.example.messenger.payload.response.LoginResponse;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.AuthService;
import com.example.messenger.validations.ResponseErrorValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;


import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LogInTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private ResponseErrorValidation responseErrorValidation;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<LoginRequest> captor;

    private LoginRequest loginRequest;

    @BeforeEach
    public void setup() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("Noyy374");
        loginRequest.setPassword("123456");
    }

    @Test
    public void loginSuccessfully() throws Exception {
        String jsonString = objectMapper.writeValueAsString(loginRequest);
        when(responseErrorValidation.mapValidationService(any())).thenReturn(null);
        when(authService.login(captor.capture(), any())).thenReturn(ResponseEntity.ok().body(new LoginResponse("TestToken")));

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TestToken")));

        verify(authService, times(1)).login(captor.capture(), any());
    }

    @Test
    public void loginErrorValidation() throws Exception {
        String jsonString = objectMapper.writeValueAsString(loginRequest);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("username", "Username cannot be empty");
        when(responseErrorValidation.mapValidationService(any())).thenReturn(ResponseEntity.badRequest().body(errorResponse));

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username cannot be empty")));

        verify(authService, never()).login(any(), any());
    }

    @Test
    public void loginIncorrectUsernameOrPassword() throws Exception {
        String jsonString = objectMapper.writeValueAsString(loginRequest);
        when(responseErrorValidation.mapValidationService(any())).thenReturn(null);
        when(authService.login(captor.capture(), any())).thenReturn(ResponseEntity.badRequest().body(new MessageResponse("Incorrect username or password")));

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Incorrect username or password")));

        verify(authService, times(1)).login(captor.capture(), any());
    }
}
