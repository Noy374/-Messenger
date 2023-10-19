package com.example.messenger.entity;

import com.example.messenger.embeddable.FriendshipKey;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "friendships")
public class Friendship {

    @EmbeddedId
    private FriendshipKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("friendId")
    @JoinColumn(name = "friend_id")
    private User friend;

    @Column
    private boolean areMessagesAllowed;

    @Column
    private boolean isFriendListVisible;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        this.areMessagesAllowed=true;
        this.isFriendListVisible=true;
        this.createdDate = LocalDateTime.now();
    }

}