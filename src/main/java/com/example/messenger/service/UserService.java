package com.example.messenger.service;


import com.example.messenger.entity.Email;
import com.example.messenger.entity.User;
import com.example.messenger.payload.request.EmailChangeRequest;
import com.example.messenger.payload.request.PasswordChangeRequest;
import com.example.messenger.payload.request.ProfileRequest;
import com.example.messenger.payload.request.RegistrationRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.repositorys.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final EmailService emailService;

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
        user.setPassword(new BCryptPasswordEncoder().encode(registrationRequest.getPassword()));
        user.setUsername(registrationRequest.getUsername());
        return  user;
    }

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
        return userRepository.getUserByUsername(username).orElse(null);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void updateProfile(ProfileRequest profileRequest) {

        User user = getUser();
        user.setSurname(profileRequest.getSurname());
        user.setName(profileRequest.getName());
        user.setUsername(profileRequest.getUsername());
               userRepository.save(user);
    }

    @Transactional
    public ResponseEntity<Object> changePassword(PasswordChangeRequest passwordChangeRequest) {
        User user = getUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (passwordEncoder.matches(passwordChangeRequest.getPassword(), user.getPassword())) {
            userRepository.updatePasswordByUsername(
                    passwordChangeRequest.getUsername(),
                    passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
            return ResponseEntity.ok().body(new MessageResponse("Password changed successfully"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Incorrect password"));
    }



        public void changeProfileStatus() {
            User user = getUser();
            user.setStatus(false);
            userRepository.save(user);
        }

    public ResponseEntity<Object> changeEmail(EmailChangeRequest emailChangeRequest) {
        if(!emailService.sendConfirmationEmail(emailChangeRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid email"));
        }
        emailService.updateEmail(getUser() ,emailChangeRequest.getEmail());
        return ResponseEntity.ok().body(new MessageResponse("Email changed successfully"));
    }
    public User getUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<User> getFriends() {
        User user = getUser();
        return new ArrayList<>(user.getFriends());
    }

    public ResponseEntity<Object> addFriend(String friendUsername) {
        User friend=getUserByUsername(friendUsername);
        System.out.println(friendUsername);
       if(friend==null)
           return ResponseEntity
                   .badRequest()
                   .body(new MessageResponse("User with username:"+friendUsername+" not found"));
       User user = getUser();

        Set<User> friends = user.getFriends();
        friends.add(friend);
        user.setFriends(friends);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("User with username:"+friendUsername+" added to your friends")) ;
    }
}
