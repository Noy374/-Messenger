package com.example.messenger.service;


import com.example.messenger.entity.Token;
import com.example.messenger.entity.User;
import com.example.messenger.repositorys.TokenRepository;
import com.example.messenger.security.EncodeOperations;
import com.example.messenger.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class TokenService {

    private  final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final EncodeOperations encoder;
    @Transactional
    public void saveToken(UserDetails userDetails, String refreshToken) {
        Token token =new Token();
        token.setRefreshToken(encoder.encode(refreshToken));
        User user= userService.getUserByUsername(userDetails.getUsername());
        token.setUser(user);
        tokenRepository.save( token);

    }

    @Transactional
    public void deleteToken(User user) {

        Token token = user.getToken();
        if (token != null) {
            user.setToken(null);
            userService.saveUser(user);
            tokenRepository.deleteById(token.getId());
        }
    }

    public boolean checkToken(String refreshToken) {
        User user = userService.getUserByUsername(jwtTokenUtils.getUsername(refreshToken));
        return encoder.matches(refreshToken, user.getToken().getRefreshToken());

    }
}
