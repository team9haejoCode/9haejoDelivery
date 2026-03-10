package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.OrderStatus;
import com.sparta._9haejodelivery.domain.UserRole;
import com.sparta._9haejodelivery.global.security.UserDetailsImpl;

import java.util.UUID;

public record OrderSearchCondition(String username, UUID storeId, OrderStatus status) {

  public static OrderSearchCondition of(OrderSearchRequestDto request, UserDetailsImpl user) {
    String role = user.getUser().getRole().getAuthority();
    String searchUsername = (role.equals(UserRole.CUSTOMER.getAuthority())) ? user.getUsername() : request.getUsername();


    return new OrderSearchCondition(searchUsername, request.getStoreId(), request.getStatus());
  }
}
