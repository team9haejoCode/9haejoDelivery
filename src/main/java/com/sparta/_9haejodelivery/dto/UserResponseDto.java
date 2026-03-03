package com.sparta._9haejodelivery.dto;

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
    private LocalDateTime updatedAt;
}
