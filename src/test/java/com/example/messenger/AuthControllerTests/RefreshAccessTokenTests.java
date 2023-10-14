package com.example.messenger.AuthControllerTests;


import com.example.messenger.entity.User;
import com.example.messenger.security.JwtTokenUtils;
import com.example.messenger.service.AuthService;
import com.example.messenger.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
public class RefreshAccessTokenTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private UserService userService;


    @Test
    public void refreshAccessTokenSuccessfully() throws Exception {
        String refreshToken = "validRefreshToken";
        MockHttpServletRequestBuilder requestBuilder = get("/refresh")
                .cookie(new Cookie("JWT_REFRESH_TOKEN", refreshToken));

        when(jwtTokenUtils.fetchTokenFromCookies(any())).thenReturn(refreshToken);
        when(jwtTokenUtils.isTokenExpired(refreshToken)).thenReturn(false);
        when(userService.getUser()).thenReturn(new User());
        when(authService.refreshAccessToken(any())).thenReturn(ResponseEntity.ok().body("New access token"));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New access token")));
    }

    @Test
    public void refreshAccessTokenWithExpiredToken() throws Exception {
        String refreshToken = "expiredRefreshToken";
        MockHttpServletRequestBuilder requestBuilder = get("/refresh")
                .cookie(new Cookie("JWT_REFRESH_TOKEN", refreshToken));

        when(jwtTokenUtils.fetchTokenFromCookies(any())).thenReturn(refreshToken);
        when(jwtTokenUtils.isTokenExpired(refreshToken)).thenReturn(true);
        when(authService.refreshAccessToken(any())).thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());


        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).refreshAccessToken(any());
    }


}
