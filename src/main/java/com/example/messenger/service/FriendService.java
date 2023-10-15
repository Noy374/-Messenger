package com.example.messenger.service;

import com.example.messenger.entity.User;
import com.example.messenger.exceptions.UserNotFoundException;
import com.example.messenger.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public List<User> getFriends()  {
        User user = userService.getUser();
        return new ArrayList<>(user.getFriends());
    }

    public void addFriend(String friendUsername) throws UserNotFoundException {
        try {
            User friend = userService.getUserByUsername(friendUsername);
            User user = userService.getUser();

            Set<User> friends = user.getFriends();
            friends.add(friend);
            user.setFriends(friends);

            userRepository.save(user);
        }catch (UsernameNotFoundException e){
            log.error("User with username {} not found", friendUsername);
            throw new UserNotFoundException("User not found");

        }
        log.info("User with username {} added to your friends", friendUsername);
    }
}
