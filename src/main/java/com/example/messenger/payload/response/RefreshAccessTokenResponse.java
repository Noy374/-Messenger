package com.example.messenger.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RefreshAccessTokenResponse {
    String token;
}