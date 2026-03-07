package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSearchResponseDto {
  private UUID orderId;
  private String username;
  private UUID storeId;
  private OrderStatus status;
  private Integer totalPrice;
  private String address;

  public static OrderSearchResponseDto from(
      Order order,
      String userName,
      UUID storeId
  ) {
    return OrderSearchResponseDto.builder()
                                 .orderId(order.getOrderId())
                                 .username(userName)
                                 .storeId(storeId)
                                 .status(order.getStatus())
                                 .totalPrice(order.getTotalPrice())
                                 .address(order.getAddress())
                                 .build();
  }
}