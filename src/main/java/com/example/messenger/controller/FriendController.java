package com.example.messenger.controller;

import com.example.messenger.entity.User;
import com.example.messenger.exceptions.FriendListCloseException;
import com.example.messenger.exceptions.IllegalFriendshipException;
import com.example.messenger.exceptions.InvalidUserStatus;
import com.example.messenger.payload.request.*;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
@Tag(
        name="Контроллер друзей" +
                "(Friend Controller)",
        description="В этом контроллере описаны методы для управление друзьями: добавление и получение друзей" +
                "(This controller describes methods for managing friends: adding and getting friends)"
)
public class FriendController {

    private final FriendService friendService;

    @Operation(
            summary = "Добавление друга" +
                    "(Add friend)",
            description = "Позволяет добавлять друга по имени пользователя" +
                    "(Allows you to add a friend by username)"
    )
    @PostMapping("/add")
    public ResponseEntity<Object> addFriend(@RequestBody AddFriendRequest addFriendRequest) {
        try {
             friendService.addFriend(addFriendRequest.getFriendUsername());
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("User with username:"+addFriendRequest.getFriendUsername()+" added to your friends")) ;
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+addFriendRequest.getFriendUsername()+" not found"));
        } catch (InvalidUserStatus e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+addFriendRequest.getFriendUsername()+" deleted account"));
        } catch (IllegalFriendshipException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+addFriendRequest.getFriendUsername()+" already among your friends"));
        }
    }
    @Operation(
            summary = "Получение всех друзей" +
                    "(Get all friends)",
            description = "Позволяет получить список всех друзей" +
                    "(Allows you to get a list of all friends)"
    )
    @GetMapping("/get_all")
    public ResponseEntity<Object> getFriends() {
        return ResponseEntity.ok(friendService.getAllFriends());
    }

    @Operation(
            summary = "Установка разрешения на сообщения" +
                    "(Set messages allowed)",
            description = "Позволяет установить разрешение на отправку сообщений другу (например, блокировка/разблокировка сообщений)" +
                    "(Allows you to set a permission to send messages to friend (for example, block/unblock messages))"
    )
    @PutMapping("/allow_messages")
    public ResponseEntity<Object> allowMessages(@RequestBody AllowMessagesRequest allowMessagesRequest) {
        try {
            friendService.setMessagesAllowed(allowMessagesRequest.getFriendUsername(), allowMessagesRequest.isAllowed());
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Messages status: "+allowMessagesRequest.isAllowed()+", set for friend with username:" +allowMessagesRequest.getFriendUsername()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+allowMessagesRequest.getFriendUsername()+" not found"));
        } catch (IllegalFriendshipException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("No friendship relation found with user:"+allowMessagesRequest.getFriendUsername()));
        }
    }

    @Operation(
            summary = "Установка видимости списка друзей" +
                    "(Set friend list visibility)",
            description = "Позволяет установить видимость списка друзей" +
                    "(Allows to set friend list visibility)"
    )
    @PutMapping("/set_visibility")
    public ResponseEntity<Object> setVisibility(@RequestBody SetVisibilityRequest setVisibilityRequest) {
        try {
            friendService.setFriendListVisible(setVisibilityRequest.getFriendUsername(), setVisibilityRequest.isVisible());
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("FriendList visibility status: "+setVisibilityRequest.isVisible()+" set for user with username:"+setVisibilityRequest.getFriendUsername()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+setVisibilityRequest.getFriendUsername()+" not found"));
        } catch (IllegalFriendshipException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("No friendship relation found with user:"+setVisibilityRequest.getFriendUsername()));
        }
    }

    @Operation(
            summary = "Установка видимости списка друзей для всех" +
                    "(Set friend list visibility for all)",
            description = "Позволяет установить видимость списка друзей для всех друзей пользователя" +
                    "(Allows to set friend list visibility for all user's friends)"
    )
    @PutMapping("/set_visibility_all")
    public ResponseEntity<Object> setVisibilityForAll(@RequestBody SetVisibilityForAllRequest setVisibilityForAllRequest) {
        friendService.hideFriendListFromAll(setVisibilityForAllRequest.isVisible());
        return ResponseEntity.ok(new MessageResponse("FriendList visibility status: " + setVisibilityForAllRequest.isVisible() + " is set for all friends"));
    }

    @Operation(
            summary = "Установка разрешения на сообщения для всех друзей" +
                    "(Set messages allowed for all)",
            description = "Позволяет установить разрешение на отправку сообщений для всех друзей пользователя" +
                    "(Allows to set the permission to send messages for all user's friends)"
    )
    @PutMapping("/allow_messages_all")
    public ResponseEntity<Object> setMessagesAllowedForAll(@RequestBody AllowMessagesForAllRequest allowMessagesForAllRequest) {
        friendService.setMessagesAllowedForAll(allowMessagesForAllRequest.isAllowed());
        return ResponseEntity.ok(new MessageResponse("Messages status: " + allowMessagesForAllRequest.isAllowed() + " is set for all friends"));
    }

    @Operation(
            summary = "Просмотр друзей пользователя" +
                    "(View ser friends)",
            description = "Позволяет просмотреть друзей  пользователя, учитывая права на просмотр" +
                    "(Allows you to view friends of user, considering viewing permissions)"
    )
    @GetMapping("/friends_of_user")
    public ResponseEntity<Object> getFriendsOfUser(@RequestParam String username) {
        try {
            List<User> friends = friendService.getFriendsOfUser(username);
            return ResponseEntity.ok(friends);
        } catch (AccessDeniedException | FriendListCloseException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You don't have permission to view the friend list of user with username:"+ username));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+username+" not found"));
        }
    }


}
