package com.sparta._9haejodelivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

  @Bean
  public AuditorAware<String> auditorProvider() {
    return () -> {
      // 1. 시큐리티 컨텍스트에서 인증 정보를 가져옴
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      // 2. 인증 정보가 없거나, 익명 사용자(Anonymous)인 경우 처리
      if (authentication == null || !authentication.isAuthenticated() ||
          authentication instanceof AnonymousAuthenticationToken) {
        return Optional.empty();
      }

      // 3. 로그인한 유저의 username 반환
      // (우리가 만든 UserDetailsImpl에서 정보를 꺼내옴)
      return Optional.of(authentication.getName());
    };
  }
}