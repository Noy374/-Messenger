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
        if (request.getRequestURI().startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt=null;
        String username=null;
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

             jwt = authHeader.substring(BEARER_TOKEN_START_INDEX);
            username = jwtTokenUtils.getUsername(jwt);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT error: ", e);
            setResponseError(response, HttpServletResponse.SC_BAD_REQUEST, "Expired JWT");

        } catch (SignatureException e) {
            logger.error("Signature error: ", e);
            setResponseError(response, HttpServletResponse.SC_FORBIDDEN, "Invalid signature");
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }



    private void setResponseUnauthorized(HttpServletResponse response, String message) throws IOException {
        setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED, message);
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