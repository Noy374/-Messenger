package com.example.messenger.service;


import com.example.messenger.entity.User;
import com.example.messenger.payload.request.LoginRequest;
import com.example.messenger.payload.request.RegistrationRequest;
import com.example.messenger.payload.response.LoginResponse;
import com.example.messenger.payload.response.MessageResponse;
import com.example.messenger.payload.response.RefreshAccessTokenResponse;
import com.example.messenger.security.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final JwtTokenUtils jwtTokenUtils;



    public ResponseEntity<Object> registration (RegistrationRequest registrationRequest){
        userService.saveUser(registrationRequest);
        if(!emailService.sendConfirmationEmail(registrationRequest.getEmail()))
            return  ResponseEntity.badRequest().body(new MessageResponse("Invalid email"));
        return ResponseEntity.ok().body("You have successfully registered.Please confirm your email");
    }


    public ResponseEntity<Object> login(LoginRequest loginRequest,  HttpServletResponse response) {


        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        } catch (Exception i) {
            return ResponseEntity.badRequest().body(new MessageResponse("Incorrect username or password"));
        }

        UserDetails userDetails= userService.loadUserByUsername(loginRequest.getUsername());
        String refreshToken= jwtTokenUtils.generateRefreshToken(userDetails);
        tokenService.saveToken(userDetails,refreshToken);
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setMaxAge(7200 * 60);//5 days
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new LoginResponse(jwtTokenUtils.generateAccessToken(userDetails)));
    }


    public ResponseEntity<Object> confirmEmailToken(String token) {
       return emailService.confirmEmailToken(token);
    }

    public ResponseEntity<Object> logOut(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user=userService.getUser();
            tokenService.deleteToken(user);
            JwtTokenUtils.deleteRefreshTokenCookie(request,response);
            return ResponseEntity.ok(new MessageResponse("Logged out successfully."));
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred during log out."));
        }
    }

    public ResponseEntity<Object> refreshAccessToken(HttpServletRequest request) {
        String refreshToken = jwtTokenUtils.fetchTokenFromCookies(request.getCookies());
        if (refreshToken == null || !tokenService.checkToken(refreshToken))
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok().body(new RefreshAccessTokenResponse(jwtTokenUtils
                .generateAccessToken(userService.getUserByUsername(jwtTokenUtils.getUsername(refreshToken)))));

    }
}