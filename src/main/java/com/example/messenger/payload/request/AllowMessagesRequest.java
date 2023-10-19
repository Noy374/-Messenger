package com.example.messenger.payload.request;


import lombok.Data;

@Data
public class AllowMessagesRequest {
    private String friendUsername;
    private boolean allowed;
}