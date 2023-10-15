package com.example.messenger.service;

import com.example.messenger.entity.User;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {

    private final UserRepository userRepository;
    private final UserService userService;
    public List<User> getFriends() {
        User user = userService.getUser();
        return new ArrayList<>(user.getFriends());
    }

    public ResponseEntity<Object> addFriend(String friendUsername) {
        User friend=userService.getUserByUsername(friendUsername);
        System.out.println(friendUsername);
        if(friend==null)
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with username:"+friendUsername+" not found"));
        User user = userService.getUser();

        Set<User> friends = user.getFriends();
        friends.add(friend);
        user.setFriends(friends);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("User with username:"+friendUsername+" added to your friends")) ;
    }
}
