package com.example.messenger.AuthControllerTests;


import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;



import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class LogOutTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;



    @Test
    public void logOutSuccessfully() throws Exception {
        when(authService.logOut(any(), any())).thenReturn(ResponseEntity.ok().body(new MessageResponse("Logged out successfully.")));

        mockMvc.perform(
                        post("/log-out")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Logged out successfully.")));

        verify(authService, times(1)).logOut(any(), any());
    }

    @Test
    public void logOutError() throws Exception {
        when(authService.logOut(any(), any())).thenReturn(ResponseEntity.badRequest().body(new MessageResponse("An error occurred during log out.")));

        mockMvc.perform(
                        post("/log-out")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("An error occurred during log out.")));

        verify(authService, times(1)).logOut(any(), any());
    }
}