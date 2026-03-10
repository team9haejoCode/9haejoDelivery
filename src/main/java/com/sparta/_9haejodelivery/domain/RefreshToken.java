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

    public RefreshToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}