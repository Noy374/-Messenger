package com.example.messenger.controller;


import com.example.messenger.exceptions.*;
import com.example.messenger.payload.request.LogOutRequest;
import com.example.messenger.payload.request.LoginRequest;
import com.example.messenger.payload.request.RegistrationRequest;
import com.example.messenger.payload.response.LoginResponse;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.payload.response.RefreshAccessTokenResponse;
import com.example.messenger.service.AuthService;
import com.example.messenger.validations.ResponseErrorValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Tag(
        name="Контроллера авторизации" +
                "(Authorization controller)",
        description="В этом контроллере описаны методы авторизации,входа и выхода " +
                "(This controller describes the authorization, login and logout methods)"
)
public class AuthController {

    private final AuthService authService;
    private final ResponseErrorValidation responseErrorValidation;
    @Operation(
            summary = "Регистрация пользователя" +
                    "(User registration)",
            description = "Позволяет зарегистрировать пользователя" +
                    "(Allows you to register a user)"
    )
    @PostMapping("/reg")
    ResponseEntity<Object> registration (@Valid @RequestBody RegistrationRequest registrationRequest, BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        try {
            authService.registration(registrationRequest);
            return ResponseEntity.ok().body(new MessageResponse("You have successfully registered. Please confirm your email."));
        } catch (EmailNotValidException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid email"));
        }
    }

    @Operation(
            summary = "Вход" +
                    "(Login)",
            description = "Позволяет  пользователям войти в свой аккаунт" +
                    "(Allows users to log into their account)"
    )
    @PostMapping("/login")
    ResponseEntity<Object> logIn(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult, HttpServletResponse response) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        try {
            LoginResponse loginResponse = authService.login(loginRequest, response);
            return ResponseEntity.ok(loginResponse);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Incorrect username or password"));
        } catch (InvalidUserStatus e) {
            return  ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @Operation(
            summary = "Подтверждение электонной почты" +
                    "(Email confirmation)",
            description = "Позволяет пользователям подтверждать свои электронные почти" +
                    "(Allows users to verify their email accounts)"
    )
    @GetMapping("/confirm")
    public ResponseEntity<Object> confirm(@RequestParam("token") String token) {
        try {
            authService.confirmEmailToken(token);
        } catch (EmailTokenNotFoundException e) {
            return  ResponseEntity.badRequest().body(new MessageResponse("Email Token Not Found."));
        }
        return ResponseEntity.ok().body(new MessageResponse("Email confirmed successfully."));
    }

    @Operation(
            summary = "Выход" +
                    "(Logout)",
            description = "Позволяет  пользователя выйти из своего аккаунта" +
                    "(Allows the user to log out of their account)"
    )
    @PostMapping("/logout")
    ResponseEntity<Object> logOut(@Valid @RequestBody LogOutRequest logOutRequest, HttpServletResponse response,BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        try {
            authService.logOut(logOutRequest.getUsername(),response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body("Logged out successfully.");
    }

    @Operation(
            summary = "Обновлениение токена доступа" +
                    "(Access Token Refresh)",
            description = "Позволяет  обновлять токены досупа" +
                    "(Allows access tokens to be updated)"
    )
    @GetMapping("/refresh")
    ResponseEntity<Object> refreshAccessToken(HttpServletRequest request){
        RefreshAccessTokenResponse response;
        try {
             response = authService.refreshAccessToken(request);

        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid or expired refresh token"));
        }
        return ResponseEntity.ok(response);
    }
}