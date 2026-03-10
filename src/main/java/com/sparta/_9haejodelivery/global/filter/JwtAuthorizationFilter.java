package com.sparta._9haejodelivery.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.common.ErrorCode;
import com.sparta._9haejodelivery.global.jwt.JwtUtil;
import com.sparta._9haejodelivery.global.security.UserDetailsImpl;
import com.sparta._9haejodelivery.global.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

import static com.sparta._9haejodelivery.global.jwt.JwtUtil.AUTHORIZATION_KEY;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        log.info("요청 들어옴! URL: " + req.getRequestURI());

        String tokenValue = jwtUtil.getJwtFromHeader(req);

        if (StringUtils.hasText(tokenValue)) {
            if (jwtUtil.validateToken(tokenValue)) {
                log.info("토큰 유효성 검사 성공");
                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
                try {
                    setAuthentication(info.getSubject(), info.get(AUTHORIZATION_KEY, String.class));
                } catch (Exception e) {
                    sendErrorResponse(res, ErrorCode.USER_INFO_MISMATCH);
                    return;
                }
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 객체 생성 및 SecurityContextHolder에 저장
    public void setAuthentication(String username, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username, role);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username,String role) {
        UserDetails userDetails = new UserDetailsImpl(username, role);
        log.info("createAuthentication 유저 권한 확인: " + userDetails.getAuthorities().toString());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.fail(errorCode.getHttpStatus(), errorCode.getMessage());

        new ObjectMapper().writeValue(response.getWriter(), apiResponse);
    }

}
