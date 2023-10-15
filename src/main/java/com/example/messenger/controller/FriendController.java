package com.example.messenger.controller;

import com.example.messenger.payload.request.AddFriendRequest;
import com.example.messenger.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/add")
    public ResponseEntity<Object> addFriend(@RequestBody AddFriendRequest addFriendRequest) {
        return friendService.addFriend(addFriendRequest.getFriendUsername());
    }

    @GetMapping("/get_all")
    public ResponseEntity<Object> getFriends() {
        return ResponseEntity.ok(friendService.getFriends());
    }
}
