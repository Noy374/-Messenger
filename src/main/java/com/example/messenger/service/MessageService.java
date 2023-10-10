package com.example.messenger.service;
import com.example.messenger.documens.Message;
import com.example.messenger.entity.User;
import com.example.messenger.repositorys.MessageRepository;
import com.example.messenger.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private  final MessageRepository messageRepository;


    public void sendMessage(String senderUsername, String recipientUsername, String text) {
        User sender = userRepository.getUserByUsername(senderUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User recipient= userRepository.getUserByUsername(recipientUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Message message = new Message();
            message.setSenderId(sender.getId());
            message.setRecipientId(recipient.getId());
            message.setText(text);
            message.setTimestamp(LocalDateTime.now());
            messageRepository.save(message);

    }

    public List<Message> getChatHistory(String senderUsername, String recipientUsername) {
        User sender = userRepository.getUserByUsername(senderUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User recipient= userRepository.getUserByUsername(recipientUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return messageRepository.findBySenderIdAndRecipientId(sender.getId(), recipient.getId());

    }
}
