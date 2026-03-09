package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.common.BusinessException;
import com.sparta._9haejodelivery.common.ErrorCode;
import com.sparta._9haejodelivery.domain.RefreshToken;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import com.sparta._9haejodelivery.dto.UserResponseDto;
import com.sparta._9haejodelivery.dto.UserSignupRequestDto;
import com.sparta._9haejodelivery.dto.UserUpdateProfileRequestDto;
import com.sparta._9haejodelivery.global.jwt.JwtUtil;
import com.sparta._9haejodelivery.repository.RefreshTokenRepository;
import com.sparta._9haejodelivery.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void signup(UserSignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword()); // 암호화!

        if (userRepository.existsById(username)) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .nickname(requestDto.getNickname())
                .address(requestDto.getAddress())
                .role(UserRole.CUSTOMER) // 기본값은 Customer
                .build();

        userRepository.save(user);
    }

    @Transactional
    public Map<String, String> reissue(String refreshTokenValue) {
        String token = jwtUtil.substringToken(refreshTokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        RefreshToken refreshToken = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALREADY_LOGOUT_USER));

        //사용자가 보낸 토큰과 DB에 저장된 토큰이 일치하는지 확인
        if (!refreshToken.getToken().equals(refreshTokenValue)) {
            throw new BusinessException(ErrorCode.USER_INFO_MISMATCH);
        }

        UserRole role = refreshToken.getRole();

        String newAccessToken = jwtUtil.createAccessToken(username, role);

        return Map.of("accessToken", newAccessToken);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getUsers(Pageable pageable) {
        return userRepository.findAllByDeletedAtIsNull(pageable).map(user -> UserResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .address(user.getAddress())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .deletedAt(user.getDeletedAt())
                .build());
    }

    @Transactional
    public UserResponseDto updateProfile(String username, UserUpdateProfileRequestDto requestDto) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newNickname = requestDto.getNickname();

        if (!user.getNickname().equals(newNickname)) {
            if (userRepository.existsByNicknameAndDeletedAtIsNull(newNickname)) {
                throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }

        user.updateProfile(newNickname, requestDto.getAddress());

        return new UserResponseDto(user);
    }

    @Transactional
    public void withdraw(String targetUsername, String requesterUsername) {
        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.ALREADY_WITHDRAWN);
        }

        user.markAsDeleted(requesterUsername);
    }

    @Transactional
    public void logout(String username) {
        if (refreshTokenRepository.existsById(username)) {
            refreshTokenRepository.deleteById(username);
        } else {
            throw new BusinessException(ErrorCode.ALREADY_LOGOUT_USER);
        }
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

}
