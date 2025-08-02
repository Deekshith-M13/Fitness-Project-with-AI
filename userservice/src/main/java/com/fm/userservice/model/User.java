package com.fm.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false, unique = true)
    private String keyCloakId;


    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;


    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.User;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

}
