package com.sparta._9haejodelivery.domain;

import com.sparta._9haejodelivery.common.BusinessException;
import com.sparta._9haejodelivery.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
  CUSTOMER("ROLE_CUSTOMER"),
  OWNER("ROLE_OWNER"),
  MANAGER("ROLE_MANAGER"),
  MASTER("ROLE_MASTER");

  private final String authority;

  public static UserRole of(String authority) {
    String target = authority.startsWith("ROLE_") ? authority : "ROLE_" + authority;
    for (UserRole role : UserRole.values()) {
      if (role.getAuthority().equals(target)) {
        return role;
      }
    }
    throw new BusinessException(ErrorCode.USER_INFO_MISMATCH);
  }
}