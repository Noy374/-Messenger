package com.example.messenger.service;
import com.example.messenger.documens.Message;
import com.example.messenger.entity.Friendship;
import com.example.messenger.entity.User;
import com.example.messenger.exceptions.InvalidUserStatus;
import com.example.messenger.repositorys.FriendshipRepository;
import com.example.messenger.repositorys.MessageRepository;
import com.example.messenger.repositorys.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    private final FriendshipRepository friendshipRepository;

    @Transactional
    public void sendMessage(String senderUsername, String recipientUsername, String text) throws UsernameNotFoundException ,AccessDeniedException{
        User sender = findUserByUsername(senderUsername);
        User recipient = findUserByUsername(recipientUsername);
        Message message = new Message();
        message.setSenderId(sender.getId());
        message.setRecipientId(recipient.getId());
        message.setText(text);
        if(recipient.getOnlyFriendsCanWrite()) {
            Optional<Friendship> optionalFriendship = friendshipRepository.findByUserAndFriend(sender, recipient);

            if (optionalFriendship.isPresent()) {
                Friendship friendship = optionalFriendship.get();
                if (friendship.isFriendListVisible()) {

                    messageRepository.save(message);
                } else {
                    throw new AccessDeniedException("The user has limited the circle of people who can write to him");
                }
            }
        }else messageRepository.save(message);
    }

    public List<Message> getChatHistory(String senderUsername, String recipientUsername) throws UsernameNotFoundException, InvalidUserStatus {
        User sender = findUserByUsername(senderUsername);
        User recipient = findUserByUsername(recipientUsername);
        if(!recipient.getStatus())
            throw new InvalidUserStatus("Invalid Status");
        return messageRepository.findBySenderIdAndRecipientId(sender.getId(), recipient.getId());
    }

    private User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }
}
