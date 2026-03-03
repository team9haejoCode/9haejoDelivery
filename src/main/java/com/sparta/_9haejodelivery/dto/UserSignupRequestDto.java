package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserSignupRequestDto {

    @Getter
    @NoArgsConstructor
    public class SignupRequestDto {
        @NotBlank
        @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "아이디는 4~10자 영문 소문자와 숫자만 가능합니다.")
        private String username;

        @NotBlank
        @Size(min = 4, max = 20)
        private String nickname;

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
                message = "비밀번호는 8~15자이며, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
        private String password;

        private String address;
        private UserRole role = UserRole.CUSTOMER;
    }

}
