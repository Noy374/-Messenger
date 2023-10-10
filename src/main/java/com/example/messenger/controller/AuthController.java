package com.example.messenger.controller;


import com.example.messenger.payload.request.LogOutRequest;
import com.example.messenger.payload.request.LoginRequest;
import com.example.messenger.payload.request.RegistrationRequest;
import com.example.messenger.service.AuthService;
import com.example.messenger.validations.ResponseErrorValidation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ResponseErrorValidation responseErrorValidation;
    @PostMapping("/reg")
    ResponseEntity<Object> registration (@Valid @RequestBody RegistrationRequest registrationRequest, BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        return authService.registration(registrationRequest);
    }

    @PostMapping("/login")
    ResponseEntity<Object> logIn(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult, HttpServletResponse response) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        return authService.login(loginRequest,response);
    }

    @GetMapping("/confirm")
    public ResponseEntity<Object> confirm(@RequestParam("token") String token) {
        return authService.confirmEmailToken(token);
    }

    @PostMapping("/log/out")
    ResponseEntity<Object> logOut(@Valid @RequestBody LogOutRequest logOutRequest, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        return authService.logOut(logOutRequest,request,response);
    }
}