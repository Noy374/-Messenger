package com.example.messenger;

import com.example.messenger.exceptions.*;
import com.example.messenger.payload.request.*;
import com.example.messenger.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testUser")
class ProfileControllerTests {

    @MockBean
    private ProfileService profileService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testUpdateProfile() throws Exception {
        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setName("NewName");
        profileRequest.setSurname("NewSurname");
        profileRequest.setUsername("NewUsername");
        doNothing().when(profileService).updateProfile(any());

        mockMvc.perform(put("/profile/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));

        verify(profileService, times(1)).updateProfile(any());
    }

    @Test
    public void testChangePassword() throws Exception {
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setPassword("password");
        passwordChangeRequest.setNewPassword("newPassword");
        passwordChangeRequest.setUsername("username");
        when(profileService.changePassword(any())).thenReturn(true);

        mockMvc.perform(put("/profile/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        verify(profileService, times(1)).changePassword(any());
    }

    @Test
    public void testChangePasswordFailed() throws Exception {
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setPassword("invalidPassword");
        passwordChangeRequest.setNewPassword("newPassword");
        passwordChangeRequest.setUsername("username");
        when(profileService.changePassword(any())).thenReturn(false);

        mockMvc.perform(put("/profile/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incorrect password"));

        verify(profileService, times(1)).changePassword(any());
    }

    @Test
    public void testChangeEmail() throws Exception, EmailTokenNotFoundException {
        EmailChangeRequest emailChangeRequest = new EmailChangeRequest();
        emailChangeRequest.setEmail("newEmail@gmail.com");
        doNothing().when(profileService).changeEmail(any());

        mockMvc.perform(put("/profile/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailChangeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email changed successfully"));

        verify(profileService, times(1)).changeEmail(any());
    }


    @Test
    public void testDeleteProfile() throws Exception {
        doNothing().when(profileService).changeProfileStatus();

        mockMvc.perform(delete("/profile/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile deleted successfully"));

        verify(profileService, times(1)).changeProfileStatus();
    }
}
