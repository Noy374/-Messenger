package com.example.messenger.exceptions;

public class FriendListCloseException extends Throwable {
    public FriendListCloseException(String friend_list_is_close) {
        super(friend_list_is_close);
    }
}
