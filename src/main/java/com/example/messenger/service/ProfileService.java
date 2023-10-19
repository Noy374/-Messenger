package com.example.messenger.service;

import com.example.messenger.entity.User;
import com.example.messenger.exceptions.EmailTokenNotFoundException;
import com.example.messenger.exceptions.UserNotFoundException;
import com.example.messenger.payload.request.EmailChangeRequest;
import com.example.messenger.payload.request.PasswordChangeRequest;
import com.example.messenger.payload.request.ProfileRequest;
import com.example.messenger.repositorys.UserRepository;
import com.example.messenger.security.EncodeOperations;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserService userService;

    private final AuthService authService;

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



    public void changeProfileStatus(boolean status, HttpServletResponse response) throws UserNotFoundException {

        User user = userService.getUser();
        if (!status)authService.logOut(user.getUsername(),response);
        user.setStatus(status);
        userRepository.save(user);
    }

    public void changeEmail(EmailChangeRequest emailChangeRequest)throws EmailTokenNotFoundException {
        if(!emailService.sendConfirmationEmail(emailChangeRequest.getEmail())) {
           throw new EmailTokenNotFoundException() ;
        }
        emailService.updateEmail(userService.getUser() ,emailChangeRequest.getEmail());
    }

    @Transactional
    public void updateFriendsListVisibility(Boolean status) {
        User user = userService.getUser();
        user.setIsFriendsListOpen(status);
        userService.saveUser(user);
    }

    public void updateMessageReceivingPermission(Boolean status) {
        User user = userService.getUser();
        user.setOnlyFriendsCanWrite(status);
        userService.saveUser(user);
    }
}
