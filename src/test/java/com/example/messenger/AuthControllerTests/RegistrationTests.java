package com.example.messenger.AuthControllerTests;

import com.example.messenger.payload.request.RegistrationRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.AuthService;
import com.example.messenger.service.EmailService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ResponseErrorValidation responseErrorValidation;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<RegistrationRequest> captor;

    private RegistrationRequest registrationRequest;

    @BeforeEach
    public void setup() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setName("Karapet");
        registrationRequest.setUsername("Noyy374");
        registrationRequest.setSurname("Svarian");
        registrationRequest.setPassword("123456");
        registrationRequest.setEmail("ksvarian.mail.ru");
    }

    @Test
    public void registrationSuccessfully() throws Exception {
        String jsonString = objectMapper.writeValueAsString(registrationRequest);
        when(responseErrorValidation.mapValidationService(any())).thenReturn(null);
        when(authService.registration(captor.capture())).thenReturn(ResponseEntity.ok().body("You have successfully registered.Please confirm your email"));

        mockMvc.perform(
                        post("/reg")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("You have successfully registered.Please confirm your email")));

        verify(authService, times(1)).registration(captor.capture());
    }

    @Test
    public void registrationErrorValidation() throws Exception {
        String jsonString = objectMapper.writeValueAsString(registrationRequest);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("username", "Username cannot be empty");
        when(responseErrorValidation.mapValidationService(any())).thenReturn(ResponseEntity.badRequest().body(errorResponse));

        mockMvc.perform(
                        post("/reg")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username cannot be empty")));

        verify(authService, never()).registration(any());
    }

    @Test
    public void registrationInvalidEmail() throws Exception {
        String jsonString = objectMapper.writeValueAsString(registrationRequest);
        when(responseErrorValidation.mapValidationService(any())).thenReturn(null);
        when(emailService.sendConfirmationEmail(registrationRequest.getEmail())).thenReturn(false);
        when(authService.registration(captor.capture())).thenReturn(ResponseEntity.badRequest().body(new MessageResponse("Invalid email")));

        mockMvc.perform(
                        post("/reg")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email")));

        verify(authService, times(1)).registration(captor.capture());
    }

    @Test
    public void registrationDuplicateEmail() throws Exception {
        String jsonString = objectMapper.writeValueAsString(registrationRequest);
        when(responseErrorValidation.mapValidationService(any())).thenReturn(null);
        when(authService.registration(captor.capture())).thenReturn(ResponseEntity.badRequest().body(new MessageResponse("Email already exists")));

        mockMvc.perform(
                        post("/reg")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email already exists")));

        verify(authService, times(1)).registration(captor.capture());
    }

    @Test
    public void registrationWeakPassword() throws Exception {
        registrationRequest.setPassword("weak");
        String jsonString = objectMapper.writeValueAsString(registrationRequest);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("password", "Password must contain at least 6 characters");
        when(responseErrorValidation.mapValidationService(any())).thenReturn(ResponseEntity.badRequest().body(errorResponse));

        mockMvc.perform(
                        post("/reg")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must contain at least 6 characters")));

        verify(authService, never()).registration(any());
    }
}

