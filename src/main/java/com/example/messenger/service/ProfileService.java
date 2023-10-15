package com.example.messenger.service;

import com.example.messenger.entity.User;
import com.example.messenger.exceptions.EmailTokenNotFoundException;
import com.example.messenger.payload.request.EmailChangeRequest;
import com.example.messenger.payload.request.PasswordChangeRequest;
import com.example.messenger.payload.request.ProfileRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.repositorys.UserRepository;
import com.example.messenger.security.EncodeOperations;
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

    private final EncodeOperations encoder;
    public void updateProfile(ProfileRequest profileRequest) {

        User user = userService.getUser();
        user.setSurname(profileRequest.getSurname());
        user.setName(profileRequest.getName());
        user.setUsername(profileRequest.getUsername());
        userRepository.save(user);
    }

    @Transactional
    public boolean changePassword(PasswordChangeRequest passwordChangeRequest) {
        User user = userService.getUser();

        boolean flag=encoder.matches(passwordChangeRequest.getPassword(), user.getPassword());
        if (flag) {
            userRepository.updatePasswordByUsername(
                    passwordChangeRequest.getUsername(),
                    encoder.encode(passwordChangeRequest.getNewPassword()));
        }
        return flag;
    }



    public void changeProfileStatus() {
        User user = userService.getUser();
        user.setStatus(false);
        userRepository.save(user);
    }

    public void changeEmail(EmailChangeRequest emailChangeRequest)throws EmailTokenNotFoundException {
        if(!emailService.sendConfirmationEmail(emailChangeRequest.getEmail())) {
           throw new EmailTokenNotFoundException() ;
        }
        emailService.updateEmail(userService.getUser() ,emailChangeRequest.getEmail());
    }
}
