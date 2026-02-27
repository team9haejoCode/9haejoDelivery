package com.sparta._9haejodelivery.domain.order;

import com.sparta._9haejodelivery.domain.order.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "P_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
  @Id
  @Column(length = 10)
  private String username;

  @Column(nullable = false, unique = true, length = 20)
  private String nickname;

  @Column(nullable = false)
  private String password;

  private String address;

  @Enumerated(EnumType.STRING)
  private UserRole role;
}