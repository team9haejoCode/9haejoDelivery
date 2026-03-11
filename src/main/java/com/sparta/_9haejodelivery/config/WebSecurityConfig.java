package com.sparta._9haejodelivery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta._9haejodelivery.global.filter.JwtAuthenticationFilter;
import com.sparta._9haejodelivery.global.filter.JwtAuthorizationFilter;
import com.sparta._9haejodelivery.global.jwt.JwtUtil;
import com.sparta._9haejodelivery.global.security.UserDetailsServiceImpl;
import com.sparta._9haejodelivery.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final RefreshTokenRepository refreshTokenRepository;
  private final ObjectMapper mapper;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws
                                                                                                Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() throws
                                                           Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, refreshTokenRepository, mapper);
    filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
    return filter;
  }

  @Bean
  public JwtAuthorizationFilter jwtAuthorizationFilter() {
    return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
                                                                    Exception {
    http.csrf((csrf) -> csrf.disable());

    http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/users/signup",
                                                                                                "/users/login")
                                                                               .permitAll()
                                                                               .requestMatchers("/users/refresh")
                                                                               .permitAll()
                                                                               .requestMatchers(HttpMethod.GET,
                                                                                                "/users")
                                                                               .hasRole("MANAGER") //유저 전체 조회
                                                                               .requestMatchers(HttpMethod.DELETE,
                                                                                                "/users/withdraw/**")
                                                                               .hasRole("MANAGER") //탈퇴 처리
                                                                               .requestMatchers(HttpMethod.GET,
                                                                                                "/users/*/profile")
                                                                               .hasRole("MANAGER")//특정 유저 상세 조회
                                                                               .requestMatchers("/error")
                                                                               .permitAll()
                                                                               .requestMatchers("/v3/api-docs/**",
                                                                                                "/swagger-ui/**",
                                                                                                "/swagger-ui.html")
                                                                               .permitAll()
                                                                               .anyRequest()
                                                                               .authenticated());

    http.logout(AbstractHttpConfigurer::disable);
    http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

}
