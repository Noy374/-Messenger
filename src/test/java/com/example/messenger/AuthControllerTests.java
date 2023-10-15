package com.example.messenger;

import com.example.messenger.exceptions.*;
import com.example.messenger.payload.request.LogOutRequest;
import com.example.messenger.payload.request.LoginRequest;
import com.example.messenger.payload.request.RegistrationRequest;
import com.example.messenger.payload.response.LoginResponse;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.payload.response.RefreshAccessTokenResponse;
import com.example.messenger.service.AuthService;
import com.example.messenger.validations.ResponseErrorValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testRegistrationSuccess() throws Exception, EmailNotValidException {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setPassword("testPassword");
        registrationRequest.setEmail("testEmail@gmail.com");
        registrationRequest.setUsername("testUsername");
        registrationRequest.setName("testName");
        registrationRequest.setSurname("testSurname");
        doAnswer(invocation -> null).when(authService).registration(any());

        mockMvc.perform(post("/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You have successfully registered. Please confirm your email."));

        verify(authService, times(1)).registration(eq(registrationRequest));
    }

    @Test
    public void testRegistrationFailed() throws Exception, EmailNotValidException {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setPassword("testPassword");
        registrationRequest.setEmail("testEmail@gmail.com");
        registrationRequest.setUsername("testUsername");
        registrationRequest.setName("testName");
        registrationRequest.setSurname("testSurname");
        doThrow(EmailNotValidException.class).when(authService).registration(any());

        mockMvc.perform(post("/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email"));

        verify(authService, times(1)).registration(eq(registrationRequest));
    }

    @Test
    public void testLoginSuccess() throws Exception, InvalidCredentialsException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUsername");
        loginRequest.setPassword("testPassword");
        when(authService.login(any(), any())).thenReturn(new LoginResponse("token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        verify(authService, times(1)).login(any(), any());
    }

    @Test
    public void testLoginFailed() throws Exception, InvalidCredentialsException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUsername");
        loginRequest.setPassword("testPassword");
        doThrow(InvalidCredentialsException.class).when(authService).login(any(), any());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incorrect username or password"));

        verify(authService, times(1)).login(any(), any());
    }

    @Test
    public void testConfirmTokenSuccess() throws Exception, EmailTokenNotFoundException {
        doNothing().when(authService).confirmEmailToken(any());

        mockMvc.perform(get("/auth/confirm").param("token", "validtoken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email confirmed successfully."));

        verify(authService, times(1)).confirmEmailToken(any());
    }

    @Test
    public void testConfirmTokenFailed() throws Exception, EmailTokenNotFoundException {
        doThrow(EmailTokenNotFoundException.class).when(authService).confirmEmailToken(any());

        mockMvc.perform(get("/auth/confirm").param("token", "invalidtoken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email Token Not Found."));

        verify(authService, times(1)).confirmEmailToken(any());
    }

    @Test
    public void testLogoutSuccess() throws Exception, UserNotFoundException {
        LogOutRequest logoutRequest = new LogOutRequest();
        logoutRequest.setUsername("username");
        doNothing().when(authService).logOut(any(), any());

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully."));

        verify(authService, times(1)).logOut(any(), any());
    }

    @Test
    public void testLogoutFailed() throws Exception, UserNotFoundException {
        LogOutRequest logoutRequest = new LogOutRequest();
        logoutRequest.setUsername("username");
        doThrow(UserNotFoundException.class).when(authService).logOut(any(), any());

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).logOut(any(), any());
    }

    @Test
    public void testRefreshTokenSuccess() throws Exception, UnauthorizedRequestException {
        when(authService.refreshAccessToken(any())).thenReturn(new RefreshAccessTokenResponse("newRefreshToken"));

        mockMvc.perform(get("/auth/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("newRefreshToken"));

        verify(authService, times(1)).refreshAccessToken(any());
    }

    @Test
    public void testRefreshTokenFailed() throws Exception, UnauthorizedRequestException {
        when(authService.refreshAccessToken(any())).thenThrow(new UnauthorizedRequestException("Invalid or expired refresh token"));

        mockMvc.perform(get("/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired refresh token"));

        verify(authService, times(1)).refreshAccessToken(any());
    }
}
