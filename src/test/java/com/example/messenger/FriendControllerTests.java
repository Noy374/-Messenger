package com.example.messenger;

import com.example.messenger.entity.User;
import com.example.messenger.service.FriendService;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.messenger.exceptions.*;
import com.example.messenger.payload.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testUser")
class FriendControllerTests {

    @MockBean
    private FriendService friendService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAddFriend() throws Exception, UserNotFoundException {
        AddFriendRequest friendRequest = new AddFriendRequest();
        friendRequest.setFriendUsername("goodUsername");
        friendRequest.setFriendUsername("friend");
        doNothing().when(friendService).addFriend(anyString());

        mockMvc.perform(post("/friend/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User with username:"+friendRequest.getFriendUsername()+" added to your friends"));

        verify(friendService, times(1)).addFriend(anyString());
    }

    @Test
    public void testAddFriend_UserNotFound() throws Exception, UserNotFoundException {
        AddFriendRequest friendRequest = new AddFriendRequest();
        friendRequest.setFriendUsername("badUsername");
        doThrow(new UserNotFoundException("User not found")).when(friendService).addFriend(anyString());

        mockMvc.perform(post("/friend/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with username:"+friendRequest.getFriendUsername()+" not found"));

        verify(friendService, times(1)).addFriend(anyString());
    }

    @Test
    public void testGetFriends() throws Exception {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUsername("friend");
        users.add(user);
        when(friendService.getFriends()).thenReturn(users);

        mockMvc.perform(get("/friend/get_all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("friend"));

        verify(friendService, times(1)).getFriends();
    }
}
