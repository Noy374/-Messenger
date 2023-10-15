package com.example.messenger.service;


import com.example.messenger.entity.Email;
import com.example.messenger.entity.User;
import com.example.messenger.payload.request.RegistrationRequest;
import com.example.messenger.repositorys.UserRepository;
import com.example.messenger.security.EncodeOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final EncodeOperations encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        log.info("User with username: {} was loaded successfully.", username);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    private User fromRegReqToUser (RegistrationRequest registrationRequest) {
        User user = new User();
        Email email = new Email();
        email.setEmailAddress(registrationRequest.getEmail());
        email.setUser(user);
        user.setEmail(email);
        user.setSurname(registrationRequest.getSurname());
        user.setName(registrationRequest.getName());
        user.setPassword(encoder.encode(registrationRequest.getPassword()));
        user.setUsername(registrationRequest.getUsername());
        return  user;
    }

    @Transactional
    public void saveUser(RegistrationRequest registrationRequest) {
        userRepository.save(fromRegReqToUser(registrationRequest));

        log.info("User with username: {} was saved successfully.", registrationRequest.getUsername());
    }

    public boolean checkUserByUsername(String username) {
        boolean userExists = userRepository.getUserByUsername(username).orElse(null) == null;

        log.info("Check for username: {} existence, exists: {}", username, !userExists);

        return userExists;
    }


    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }


    public User getUser() throws UsernameNotFoundException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


}
