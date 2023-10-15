package com.example.messenger.controller;

import com.example.messenger.exceptions.UserNotFoundException;
import com.example.messenger.payload.request.AddFriendRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+addFriendRequest.getFriendUsername()+" not found"));
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
        return ResponseEntity.ok(friendService.getFriends());
    }
}
