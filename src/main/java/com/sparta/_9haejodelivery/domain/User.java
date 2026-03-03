package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity{

    @Id
    @Column(name = "username", length = 10, nullable = false)
    private String username;

    @Column(name = "nickname", length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "address", length = 100)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public void updateProfile(String nickname, String password, String address) {
        if (nickname != null) this.nickname = nickname;
        if (password != null) this.password = password;
        if (address != null) this.address = address;
    }
}
