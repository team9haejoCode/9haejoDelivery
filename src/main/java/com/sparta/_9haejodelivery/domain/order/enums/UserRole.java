package com.sparta._9haejodelivery.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
  CUSTOMER("고객"),
  OWNER("사장님"),
  MANAGER("매니저"),
  MASTER("관리자");

  private final String description;
}