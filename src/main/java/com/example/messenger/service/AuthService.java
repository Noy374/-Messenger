package com.example.messenger.service;


import com.example.messenger.entity.User;
import com.example.messenger.exceptions.*;
import com.example.messenger.payload.request.LoginRequest;
import com.example.messenger.payload.request.RegistrationRequest;
import com.example.messenger.payload.response.LoginResponse;
import com.example.messenger.payload.response.RefreshAccessTokenResponse;
import com.example.messenger.security.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final JwtTokenUtils jwtTokenUtils;

    public void registration(RegistrationRequest registrationRequest) throws EmailNotValidException {
        userService.saveUser(registrationRequest);
        if (!emailService.sendConfirmationEmail(registrationRequest.getEmail())) {
            throw new EmailNotValidException("Invalid email");
        }
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) throws InvalidCredentialsException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
            String refreshToken = jwtTokenUtils.generateRefreshToken(userDetails);
            tokenService.saveToken(userDetails, refreshToken);
            this.createCookie(response, refreshToken);
            return new LoginResponse(jwtTokenUtils.generateAccessToken(userDetails));
        } catch (Exception e) {
            throw new InvalidCredentialsException("Incorrect username or password");
        }
    }

    private void createCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        int age = 30 * 24 * 60 * 60;
        refreshTokenCookie.setMaxAge(age);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);
    }

    public void confirmEmailToken(String token) throws EmailTokenNotFoundException {
        emailService.confirmEmailToken(token);
    }

    public void logOut(String username,HttpServletResponse response) throws UserNotFoundException {
        try {
            User user = userService.getUserByUsername(username);
            tokenService.deleteToken(user);
            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            response.addCookie(refreshTokenCookie);
        } catch (UsernameNotFoundException exception) {
            throw new UserNotFoundException("User not found.");
        }
    }

    public RefreshAccessTokenResponse refreshAccessToken(HttpServletRequest request) throws UnauthorizedRequestException {

        String refreshToken = jwtTokenUtils.fetchTokenFromCookies(request.getCookies());
        if (refreshToken == null || !tokenService.checkToken(refreshToken)) {

            throw new UnauthorizedRequestException("Token is invalidated");
        }
        String username = jwtTokenUtils.getUsername(refreshToken);
        UserDetails userDetails = userService.getUserByUsername(username);
        return new RefreshAccessTokenResponse(jwtTokenUtils.generateAccessToken(userDetails));
    }
}