package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderUpdateResponseDto {
  private UUID orderId;
  private String username;
  private String address;
  private OrderStatus status;
  private LocalDateTime updatedAt;

  public static OrderUpdateResponseDto from(
      Order order
  ) {
    return OrderUpdateResponseDto.builder()
                                 .orderId(order.getOrderId())
                                 .status(order.getStatus())
                                 .address(order.getAddress())
                                 .username(order.getUser().getUsername())
                                 .updatedAt(order.getUpdatedAt())
                                 .build();
  }
}