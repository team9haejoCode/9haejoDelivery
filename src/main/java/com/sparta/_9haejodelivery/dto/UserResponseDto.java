package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private String username;
    private String nickname;
    private String address;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.address = user.getAddress();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.deletedAt = user.getDeletedAt();
    }
}
