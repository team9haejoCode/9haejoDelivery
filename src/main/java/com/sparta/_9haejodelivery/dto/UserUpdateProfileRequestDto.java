package com.sparta._9haejodelivery.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateProfileRequestDto {
    @Size(min = 4, max = 20)
    private String nickname;
    private String password;
    private String address;
}
