package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.global.security.UserDetailsImpl;
import com.sparta._9haejodelivery.domain.OrderStatus;
import com.sparta._9haejodelivery.domain.UserRole;

import java.util.UUID;

public record OrderSearchCondition(String username, UUID storeId, OrderStatus status) {

  public static OrderSearchCondition of(
      com.sparta._9haejodelivery.dto.order.request.OrderSearchRequestDto request, UserDetailsImpl user) {
        String role = user.getAuthorities()
                                 .iterator()
                                 .next()
                                 .getAuthority();
    String searchUsername = (role.equals(UserRole.CUSTOMER.getAuthority())) ? user.getUsername() : request.getUsername();


    return new OrderSearchCondition(searchUsername, request.getStoreId(), request.getStatus());
  }
}
