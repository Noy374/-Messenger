package com.example.messenger.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Token {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String refreshToken;
    private LocalDateTime createdDate;
    private LocalDateTime expirationDate;

    @OneToOne
    private User user;
    @Transient

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.expirationDate=createdDate.plusDays(5);
    }
}
