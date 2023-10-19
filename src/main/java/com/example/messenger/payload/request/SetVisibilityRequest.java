package com.example.messenger.payload.request;


import lombok.Data;

@Data
public class SetVisibilityRequest {
    private String friendUsername;
    private boolean visible;
}