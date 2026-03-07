package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_refresh_token")
public class RefreshToken {

    @Id
    private String username;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public RefreshToken(String username, String token, UserRole role) {
        this.username = username;
        this.token = token;
        this.role = role;
    }

    public void updateToken(String newToken, UserRole newRole) {
        this.token = newToken;
        this.role = newRole;
    }}