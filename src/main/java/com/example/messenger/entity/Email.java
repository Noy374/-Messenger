package com.example.messenger.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String emailAddress;
    String token;
    Boolean status;
    @OneToOne
    private User user;
    @PrePersist
    protected void onCreate() {
        this.status = false;
    }
}
