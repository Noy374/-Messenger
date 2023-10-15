package com.example.messenger.service;
import com.example.messenger.documens.Message;
import com.example.messenger.entity.User;
import com.example.messenger.repositorys.MessageRepository;
import com.example.messenger.repositorys.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public void sendMessage(String senderUsername, String recipientUsername, String text) throws UsernameNotFoundException {
        User sender = findUserByUsername(senderUsername);
        User recipient = findUserByUsername(recipientUsername);

        Message message = new Message();
        message.setSenderId(sender.getId());
        message.setRecipientId(recipient.getId());
        message.setText(text);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }

    public List<Message> getChatHistory(String senderUsername, String recipientUsername) throws UsernameNotFoundException{
        User sender = findUserByUsername(senderUsername);
        User recipient = findUserByUsername(recipientUsername);

        return messageRepository.findBySenderIdAndRecipientId(sender.getId(), recipient.getId());
    }

    private User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }
}
