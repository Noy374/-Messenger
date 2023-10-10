package com.example.messenger.controller;

import com.example.messenger.payload.request.EmailChangeRequest;
import com.example.messenger.payload.request.PasswordChangeRequest;
import com.example.messenger.payload.request.ProfileRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.UserService;
import com.example.messenger.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;
    private final ResponseErrorValidation responseErrorValidation;

    @PutMapping("/update")
    public ResponseEntity<Object> updateProfile(@Valid @RequestBody ProfileRequest profileRequest, BindingResult bindingResult) {

        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        userService.updateProfile(profileRequest);
        return  ResponseEntity.ok().body(new MessageResponse("Profile updated successfully"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest,BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        return userService.changePassword(passwordChangeRequest);
    }

    @PutMapping("/change-email")
    public ResponseEntity<Object> changeEmail(@Valid @RequestBody EmailChangeRequest emailChangeRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        return userService.changeEmail(emailChangeRequest);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteProfile() {
        userService.changeProfileStatus();
        return  ResponseEntity.ok().body(new MessageResponse("Profile deleted successfully"));
    }

}