package com.example.messenger.service;

import com.example.messenger.embeddable.FriendshipKey;
import com.example.messenger.entity.Friendship;
import com.example.messenger.entity.User;
import com.example.messenger.exceptions.FriendListCloseException;
import com.example.messenger.exceptions.IllegalFriendshipException;
import com.example.messenger.exceptions.InvalidUserStatus;
import com.example.messenger.repositorys.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserService userService;
    private final FriendshipRepository friendshipRepository;



    @Transactional
    public void addFriend(String friendUsername) throws UsernameNotFoundException, InvalidUserStatus, IllegalFriendshipException {
        User user = userService.getUser();
        User friend = userService.getUserByUsername(friendUsername);
        if(!friend.getStatus())
            throw new InvalidUserStatus("Your account has been deactivated, please reactivate it");

        if (friendshipExists(user, friend)) {
            throw new IllegalFriendshipException("They are already friends.");
        } else {
            Friendship friendship = new Friendship();
            friendship.setId(new FriendshipKey());
            friendship.setUser(user);
            friendship.setFriend(friend);
            friendshipRepository.save(friendship);
        }
    }

    public List<User> getAllFriends() {
        User user =userService.getUser();
        return friendshipRepository.findByUser(user).stream().map(Friendship::getFriend).collect(Collectors.toList());
    }

    private boolean friendshipExists(User user, User friend) {
        return friendshipRepository.findByUserAndFriend(user, friend).isPresent() ||
                friendshipRepository.findByUserAndFriend(friend, user).isPresent();
    }

    @Transactional
    public void setMessagesAllowed(String friendUsername, boolean allowed) throws UsernameNotFoundException, IllegalFriendshipException {
        User user = userService.getUser();
        User friend = userService.getUserByUsername(friendUsername);
        Optional<Friendship> optionalFriendship = friendshipRepository.findByUserAndFriend(user, friend);

        if (optionalFriendship.isPresent()) {
            Friendship friendship = optionalFriendship.get();
            friendship.setAreMessagesAllowed(allowed);
            friendshipRepository.save(friendship);
        } else {
            throw new IllegalFriendshipException("Friendship does not exist.");
        }
    }

    @Transactional
    public void setFriendListVisible(String friendUsername, boolean visible) throws UsernameNotFoundException, IllegalFriendshipException {
        User user = userService.getUser();
        User friend = userService.getUserByUsername(friendUsername);
        Optional<Friendship> optionalFriendship = friendshipRepository.findByUserAndFriend(user, friend);

        if (optionalFriendship.isPresent()) {
            Friendship friendship = optionalFriendship.get();
            friendship.setFriendListVisible(visible);
            friendshipRepository.save(friendship);
        } else {
            throw new IllegalFriendshipException("Friendship does not exist.");
        }
    }



    @Transactional
    public void hideFriendListFromAll(boolean visible) {
        User user = userService.getUser();
        List<Friendship> friendships = friendshipRepository.findByUser(user);
        for (Friendship friendship : friendships) {
            friendship.setFriendListVisible(visible);
        }
        friendshipRepository.saveAll(friendships);
    }

    @Transactional
    public void setMessagesAllowedForAll(boolean allowed) {
        User user = userService.getUser();
        List<Friendship> friendships = friendshipRepository.findByUser(user);
        for (Friendship friendship : friendships) {
            friendship.setAreMessagesAllowed(allowed);
        }
        friendshipRepository.saveAll(friendships);
    }
    public List<User> getFriendsOfUser(String username) throws UsernameNotFoundException, FriendListCloseException {

        User friend = userService.getUserByUsername(username);
        if(friend.getIsFriendsListOpen())
            return friendshipRepository.findByUser(friend).stream().map(Friendship::getFriend).collect(Collectors.toList());
        else {
              User user = userService.getUser();
            Optional<Friendship> optionalFriendship = friendshipRepository.findByUserAndFriend(user, friend);


            if (optionalFriendship.isPresent()) {
                Friendship friendship = optionalFriendship.get();
                if (friendship.isFriendListVisible()) {
                    return friendshipRepository.findByUser(friend).stream().map(Friendship::getFriend).collect(Collectors.toList());
                } else {
                    throw new AccessDeniedException("Friend's friend list is not visible");
                }
            } else {
                throw new FriendListCloseException("Friend list is close");
            }
        }
    }

}
