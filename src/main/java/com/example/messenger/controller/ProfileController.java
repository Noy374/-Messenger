package com.example.messenger.controller;

import com.example.messenger.exceptions.EmailTokenNotFoundException;
import com.example.messenger.exceptions.UserNotFoundException;
import com.example.messenger.payload.request.EmailChangeRequest;
import com.example.messenger.payload.request.PasswordChangeRequest;
import com.example.messenger.payload.request.ProfileRequest;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.service.ProfileService;
import com.example.messenger.validations.ResponseErrorValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(
        name = "Контроллер профиля" +
                "(Profile Controller)",
        description = "В этом контроллере описаны методы для работы с профилем пользователя" +
                "(This controller describes the methods for working with a user profile)"
)
public class ProfileController {

    private final ProfileService profileService;
    private final ResponseErrorValidation responseErrorValidation;

    @Operation(
            summary = "Обновление профиля" +
                    "(Profile update)",
            description = "Позволяет пользователю обновить свой профиль" +
                    "(Allows the user to update their profile)"
    )
    @PutMapping("/update")
    public ResponseEntity<Object> updateProfile(@Valid @RequestBody ProfileRequest profileRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        profileService.updateProfile(profileRequest);
        return ResponseEntity.ok().body(new MessageResponse("Profile updated successfully"));
    }

    @Operation(
            summary = "Изменение пароля" +
                    "(Change password)",
            description = "Позволяет пользователю изменить свой пароль" +
                    "(Allows the user to change their password)"
    )
    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        if(profileService.changePassword(passwordChangeRequest))
            return ResponseEntity.ok().body(new MessageResponse("Password changed successfully"));
        return ResponseEntity.badRequest().body(new MessageResponse("Incorrect password"));
    }

    @Operation(
            summary = "Изменение электронной почты" +
                    "(Change email)",
            description = "Позволяет пользователю изменить свой адрес электронной почты" +
                    "(Allows the user to change their email address)"
    )
    @PutMapping("/change-email")
    public ResponseEntity<Object> changeEmail(@Valid @RequestBody EmailChangeRequest emailChangeRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        try {
            profileService.changeEmail(emailChangeRequest);

        } catch (EmailTokenNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid email"));
        }
        return ResponseEntity.ok().body(new MessageResponse("Email changed successfully"));
    }

    @Operation(
            summary = "Удаление профиля" +
                    "(Profile delete)",
            description = "Позволяет пользователю удалить свой профиль" +
                    "(Allows the user to delete their profile)"
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteProfile(HttpServletResponse response) {
        try {
            profileService.changeProfileStatus(false,response);
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User  not found"));
        }
        return ResponseEntity.ok().body(new MessageResponse("Profile deleted successfully"));
    }

    @Operation(
            summary = "Восстановление профиля" +
                    "(Profile restore)",
            description = "Позволяет пользователю Восстановить свой профиль" +
                    "(Allows the user to restore their profile)"
    )
    @DeleteMapping("/restore")
    public ResponseEntity<Object> restoreProfile(HttpServletResponse response) {
        try {
            profileService.changeProfileStatus(true,response);
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User  not found"));
        }
        return ResponseEntity.ok().body(new MessageResponse("Profile restored successfully"));
    }


    @Operation(
            summary = "Update Friends List Visibility Status",
            description = "Allows the user to update the visibility status of their friends list"
    )
    @PutMapping("/update-friends-list-visibility")
    public ResponseEntity<Object> updateFriendsListVisibility(@RequestBody Boolean status) {
        profileService.updateFriendsListVisibility(status);
        return ResponseEntity.ok().body(new MessageResponse("Friends list visibility updated successfully"));
    }

    @Operation(
            summary = "Update Message Receiving Permission",
            description = "Allows the user to update the permission for receiving messages from non-friends"
    )
    @PutMapping("/update-message-receiving-permission")
    public ResponseEntity<Object> updateMessageReceivingPermission(@RequestBody Boolean status) {
        profileService.updateMessageReceivingPermission(status);
        return ResponseEntity.ok().body(new MessageResponse("Message receiving permission updated successfully"));
    }
}
