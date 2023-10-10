package com.example.messenger.security;


import com.example.messenger.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final int BEARER_TOKEN_START_INDEX = 7;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }
            if (!jwtTokenUtils.isBearerToken(authHeader)) {
                logger.warn("Unauthorized request, bearer token missing.");
                setResponseUnauthorized(response, "Bearer token missing or invalid.");
                return;
            }

            final String jwt = authHeader.substring(BEARER_TOKEN_START_INDEX);
            if (jwtTokenUtils.isTokenExpired(jwt)) {
                logger.info("JWT has expired, refreshing now.");
                refreshJwtToken(request, response, jwt);
            } else {
                final String username = jwtTokenUtils.getUsername(jwt);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    final UserDetails userDetails = userService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                }
                logger.info("JWT validated, processing request...");
                filterChain.doFilter(request, response);
            }

        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT error: ", e);
            setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT");

        } catch (SignatureException e) {
            logger.error("Signature error: ", e);
            setResponseError(response, HttpServletResponse.SC_FORBIDDEN, "Invalid signature");
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
        }

    }

    private void refreshJwtToken(HttpServletRequest request, HttpServletResponse response, String jwt) throws IOException {
        Objects.requireNonNull(jwt, "JWT must not be null.");

        final String refreshToken = jwtTokenUtils.fetchTokenFromCookies(request.getCookies());
        if (refreshToken == null || jwtTokenUtils.isTokenExpired(refreshToken)) {
            setResponseUnauthorized(response, "Refresh token is expired or missing.");
            return;
        }

        final String username = jwtTokenUtils.getUsername(jwt);
        if (username == null || username.trim().isEmpty()) {
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Username is missing in JWT.");
            return;
        }

        final String newAccessToken = jwtTokenUtils.generateAccessToken(userService.loadUserByUsername(username));
        setResponseNewAccessToken(response, newAccessToken);
    }

    private void setResponseUnauthorized(HttpServletResponse response, String message) throws IOException {
        setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    private void setResponseNewAccessToken(HttpServletResponse response, String newAccessToken) throws IOException {
        Objects.requireNonNull(newAccessToken, "New access token must not be null.");
        sendResponse(response, HttpServletResponse.SC_OK, Collections.singletonMap("accessToken", newAccessToken));
    }

    private void setResponseError(HttpServletResponse response, int status, String message) throws IOException {
        Objects.requireNonNull(message, "Error message must not be null.");
        sendResponse(response, status, Collections.singletonMap("error", message));
    }

    private void sendResponse(HttpServletResponse response, int status, Map<String, Object> responseBody) throws IOException {
        response.setStatus(status);
        response.setContentType(JSON_CONTENT_TYPE);
        try (PrintWriter out = response.getWriter()) {
            out.write(objectMapper.writeValueAsString(responseBody));
        }
    }
}