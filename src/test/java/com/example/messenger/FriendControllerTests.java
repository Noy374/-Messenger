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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public void testAddFriend() throws Exception, UserNotFoundException, InvalidUserStatus, IllegalFriendshipException {
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
    public void testAddFriend_UserNotFound() throws Exception, UsernameNotFoundException, InvalidUserStatus, IllegalFriendshipException {
        AddFriendRequest friendRequest = new AddFriendRequest();
        friendRequest.setFriendUsername("badUsername");
        doThrow(new UsernameNotFoundException("User not found")).when(friendService).addFriend(anyString());

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
        when(friendService.getAllFriends()).thenReturn(users);

        mockMvc.perform(get("/friend/get_all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("friend"));

        verify(friendService, times(1)).getAllFriends();
    }

    @Test
    public void testAllowMessages() throws Exception, IllegalFriendshipException {
        AllowMessagesRequest request = new AllowMessagesRequest();
        request.setFriendUsername("friend");
        request.setAllowed(true);

        doNothing().when(friendService).setMessagesAllowed(anyString(), anyBoolean());

        mockMvc.perform(put("/friend/allow_messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Messages status: "+request.isAllowed()+", set for friend with username:" +request.getFriendUsername()));

        verify(friendService, times(1)).setMessagesAllowed(anyString(), anyBoolean());
    }

    @Test
    public void testAllowMessages_UserNotFound() throws Exception, IllegalFriendshipException {
        AllowMessagesRequest request = new AllowMessagesRequest();
        request.setFriendUsername("badUsername");
        request.setAllowed(false);

        doThrow(new UsernameNotFoundException("User not found")).when(friendService).setMessagesAllowed(anyString(), anyBoolean());

        mockMvc.perform(put("/friend/allow_messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with username:"+request.getFriendUsername()+" not found"));

        verify(friendService, times(1)).setMessagesAllowed(anyString(), anyBoolean());
    }

    @Test
    public void testAddFriend_InvalidUserStatus() throws Exception, InvalidUserStatus, IllegalFriendshipException {
        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setFriendUsername("usernameWithInvalidStatus");

        doThrow(new InvalidUserStatus("This account has been deactivated"))
                .when(friendService).addFriend(anyString());

        mockMvc.perform(post("/friend/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addFriendRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with username:"+addFriendRequest.getFriendUsername()+" deleted account"));

        verify(friendService, times(1)).addFriend(anyString());
    }

    @Test
    public void testAddFriend_IllegalFriendshipException() throws Exception, InvalidUserStatus, IllegalFriendshipException {
        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setFriendUsername("usernameWithExistingFriendship");
        doThrow(new IllegalFriendshipException("They are already friends."))
                .when(friendService).addFriend(anyString());

        mockMvc.perform(post("/friend/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addFriendRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with username:"+addFriendRequest.getFriendUsername()+" already among your friends"));

        verify(friendService, times(1)).addFriend(anyString());
    }


    @Test
    public void testVisibilitySetOnFriend() throws Exception, IllegalFriendshipException {
        SetVisibilityRequest request = new SetVisibilityRequest();
        request.setFriendUsername("friend");
        request.setVisible(true);

        doNothing().when(friendService).setFriendListVisible(anyString(), anyBoolean());

        mockMvc.perform(put("/friend/set_visibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("FriendList visibility status: "+request.isVisible()+" set for user with username:"+request.getFriendUsername()));

        verify(friendService, times(1)).setFriendListVisible(anyString(), anyBoolean());
    }

    @Test
    public void testVisibilitySetForAll() throws Exception {
        SetVisibilityForAllRequest request = new SetVisibilityForAllRequest();
        request.setVisible(true);

        doNothing().when(friendService).hideFriendListFromAll(anyBoolean());

        mockMvc.perform(put("/friend/set_visibility_all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("FriendList visibility status: " + request.isVisible() + " is set for all friends"));

        verify(friendService, times(1)).hideFriendListFromAll(anyBoolean());
    }

    @Test
    public void testAllowMessagesForAll() throws Exception {
        AllowMessagesForAllRequest request = new AllowMessagesForAllRequest();
        request.setAllowed(true);

        doNothing().when(friendService).setMessagesAllowedForAll(anyBoolean());

        mockMvc.perform(put("/friend/allow_messages_all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Messages status: " + request.isAllowed() + " is set for all friends"));

        verify(friendService, times(1)).setMessagesAllowedForAll(anyBoolean());
    }

    @Test
    public void testGetFriendsOfFriend() throws Exception, FriendListCloseException {
        List<User> friendFriends = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("friend1");
        User user2 = new User();
        user2.setUsername("friend2");
        friendFriends.add(user1);
        friendFriends.add(user2);
        when(friendService.getFriendsOfUser("friend")).thenReturn(friendFriends);

        mockMvc.perform(get("/friend/friends_of_user")
                        .param("username", "friend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("friend1"))
                .andExpect(jsonPath("$[1].username").value("friend2"));

        verify(friendService, times(1)).getFriendsOfUser("friend");
    }
    @Test
    public void testGetFriendsOfFriendUsernameDoesNotExist() throws Exception, FriendListCloseException {
        String invalidUsername = "invalid_username";

        when(friendService.getFriendsOfUser(invalidUsername)).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(get("/friend/friends_of_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", invalidUsername))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with username:"+invalidUsername+" not found"));

        verify(friendService, times(1)).getFriendsOfUser(invalidUsername);
    }

    @Test
    public void testGetFriendsOfFriendAccessDenied() throws Exception, FriendListCloseException {
        String deniedUsername = "denied_username";

        when(friendService.getFriendsOfUser(deniedUsername)).thenThrow(new AccessDeniedException("Friend's friend list is not visible"));

        mockMvc.perform(get("/friend/friends_of_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", deniedUsername))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You don't have permission to view the friend list of user with username:"+ deniedUsername));

        verify(friendService, times(1)).getFriendsOfUser(deniedUsername);
    }



}
