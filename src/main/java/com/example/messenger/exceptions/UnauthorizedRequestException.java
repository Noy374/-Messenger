package com.example.messenger.exceptions;

public class UnauthorizedRequestException extends Throwable {
    public UnauthorizedRequestException(String message) {
        super(message);
    }
}
