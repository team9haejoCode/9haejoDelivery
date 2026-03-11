package com.sparta._9haejodelivery.global.security;

import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class UserDetailsImpl implements UserDetails {

  private final User user;
  private final String username;
  private final UserRole role;
  private final String password;

  // DB 조회 시 사용하는 생성자
  public UserDetailsImpl(User user) {
    this.user = user;
    this.username = user.getUsername();
    this.role = user.getRole();
    this.password = user.getPassword();
  }

  // [수정] DB를 거치지 않고 토큰 정보로만 생성하는 생성자
  public UserDetailsImpl(String username, String roleStr) {
    this.username = username;
    this.role = UserRole.of(roleStr);
    this.password = null;
    // 가짜 User 객체 생성 (ID가 필요하다면 토큰에 ID도 담아야 함)
    this.user = User.builder().username(username).role(this.role).build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    String authority = role.getAuthority();

    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(simpleGrantedAuthority);

    return authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }
}
