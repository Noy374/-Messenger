package com.example.messenger.exceptions;

public class EmailNotValidException extends Throwable {
    public EmailNotValidException(String message) {
        super(message);
    }
}
