package com.example.messenger.payload.request;


import lombok.Data;

@Data
public class AllowMessagesForAllRequest {
    private boolean allowed;
}