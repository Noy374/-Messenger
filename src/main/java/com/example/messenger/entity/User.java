package com.example.messenger.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true)
    private String username;
    @Column(nullable = false)
    private String surname;


    private String password;
    @Column(nullable = false)
    private Boolean status;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Email email;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Token token;
    @PrePersist
    protected void onCreate() {
        this.status=true;
        this.createdDate = LocalDateTime.now();
    }

}