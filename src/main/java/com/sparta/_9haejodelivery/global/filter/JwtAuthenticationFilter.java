package com.sparta._9haejodelivery.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.common.ErrorCode;
import com.sparta._9haejodelivery.domain.RefreshToken;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import com.sparta._9haejodelivery.dto.UserLoginRequestDto;
import com.sparta._9haejodelivery.global.jwt.JwtUtil;
import com.sparta._9haejodelivery.global.security.UserDetailsImpl;
import com.sparta._9haejodelivery.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;


    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        setFilterProcessesUrl("/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword(), null)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = ((UserDetailsImpl) authResult.getPrincipal()).getUser();

        if (user.getDeletedAt() != null) {
            sendErrorResponse(response, ErrorCode.ALREADY_WITHDRAWN);
            return;
        }

        String username = user.getUsername();
        UserRole role = user.getRole();

        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username);

        refreshTokenRepository.findById(username)
                .ifPresentOrElse(
                        // 1. 이미 있으면? 값만 업데이트
                        (existingToken) -> {
                            existingToken.updateToken(refreshToken);
                            refreshTokenRepository.save(existingToken);
                        },
                        // 2. 없으면? 새로 만들어서 저장
                        () -> {
                            RefreshToken newToken = new RefreshToken(username, refreshToken);
                            refreshTokenRepository.save(newToken);
                        }
                );

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.success(
                HttpStatus.OK,
                "로그인이 성공적으로 처리되었습니다.",
                Map.of("accessToken", accessToken,
                        "refreshToken", refreshToken)
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), apiResponse);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        sendErrorResponse(response, ErrorCode.INVALID_INPUT_VALUE);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.fail(errorCode.getHttpStatus(), errorCode.getMessage());

        new ObjectMapper().writeValue(response.getWriter(), apiResponse);
    }


}
