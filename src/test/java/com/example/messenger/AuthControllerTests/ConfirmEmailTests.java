package com.example.messenger.AuthControllerTests;


import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;



import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
public class ConfirmEmailTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;


    @BeforeEach
    public void setup() {
    }

    @Test
    public void confirmEmailSuccessfully() throws Exception {
        when(authService.confirmEmailToken(any())).thenReturn(ResponseEntity.ok().body(new MessageResponse("Email successfully confirmed")));

        mockMvc.perform(
                        get("/confirm")
                                .param("token", "valid_token")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email successfully confirmed")));

        verify(authService, times(1)).confirmEmailToken(any());
    }

    @Test
    public void confirmEmailFailure() throws Exception {
        when(authService.confirmEmailToken(any())).thenReturn(ResponseEntity.badRequest().body(new MessageResponse("Try confirming your email again")));

        mockMvc.perform(
                        get("/confirm")
                                .param("token", "invalid_token")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Try confirming your email again")));

        verify(authService, times(1)).confirmEmailToken(any());
    }
}
