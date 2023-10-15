package com.example.messenger.service;

import com.example.messenger.entity.User;
import com.example.messenger.payload.request.EmailChangeRequest;
import com.example.messenger.payload.request.PasswordChangeRequest;
import com.example.messenger.payload.request.ProfileRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserService userService;



    public void updateProfile(ProfileRequest profileRequest) {

        User user = userService.getUser();
        user.setSurname(profileRequest.getSurname());
        user.setName(profileRequest.getName());
        user.setUsername(profileRequest.getUsername());
        userRepository.save(user);
    }

    @Transactional
    public ResponseEntity<Object> changePassword(PasswordChangeRequest passwordChangeRequest) {
        User user = userService.getUser();
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
        User user = userService.getUser();
        user.setStatus(false);
        userRepository.save(user);
    }

    public ResponseEntity<Object> changeEmail(EmailChangeRequest emailChangeRequest) {
        if(!emailService.sendConfirmationEmail(emailChangeRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid email"));
        }
        emailService.updateEmail(userService.getUser() ,emailChangeRequest.getEmail());
        return ResponseEntity.ok().body(new MessageResponse("Email changed successfully"));
    }
}
