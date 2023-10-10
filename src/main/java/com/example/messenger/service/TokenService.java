package com.example.messenger.service;


import com.example.messenger.entity.Token;
import com.example.messenger.entity.User;
import com.example.messenger.repositorys.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class TokenService {

    private  final TokenRepository tokenRepository;
    private final UserService userService;
    public void saveToken(UserDetails userDetails, String refreshToken) {
        Token token =new Token();
        token.setToken(new BCryptPasswordEncoder().encode(refreshToken));
        User user= userService.getUserByUsername(userDetails.getUsername());
        token.setUser(user);
        tokenRepository.save( token);

    }

    @Transactional
    public void deleteToken(UserDetails userDetails) {
        User user= userService.getUserByUsername(userDetails.getUsername());
        Token token = user.getToken();
        if (token != null) {
            user.setToken(null);
            userService.saveUser(user);
            tokenRepository.deleteById(token.getId());
        }
    }
}
