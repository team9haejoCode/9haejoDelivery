package com.sparta._9haejodelivery.service;

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
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
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
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        RefreshToken refreshToken = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("로그아웃된 유저이거나 토큰이 존재하지 않습니다."));

        //사용자가 보낸 토큰과 DB에 저장된 토큰이 일치하는지 확인
        if (!refreshToken.getToken().equals(refreshTokenValue)) {
            throw new IllegalArgumentException("토큰 정보가 일치하지 않습니다. 다시 로그인해주세요.[권한변경]");
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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없거나 탈퇴한 사용자입니다."));

        String newNickname = requestDto.getNickname();

        if (!user.getNickname().equals(newNickname)) {
            if (userRepository.existsByNicknameAndDeletedAtIsNull(newNickname)) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
        }

        user.updateProfile(newNickname, requestDto.getAddress());

        return new UserResponseDto(user);
    }

    @Transactional
    public void withdraw(String targetUsername, String requesterUsername) {
        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("이미 탈퇴 처리된 사용자입니다.");
        }

        user.markAsDeleted(requesterUsername);
    }

    @Transactional
    public void logout(String username) {
        if (refreshTokenRepository.existsById(username)) {
            refreshTokenRepository.deleteById(username);
        } else {
            throw new IllegalArgumentException("이미 로그아웃 상태이거나 토큰이 존재하지 않습니다.");
        }
    }


}
