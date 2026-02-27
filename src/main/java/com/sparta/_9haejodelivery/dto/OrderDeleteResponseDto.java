package com.sparta._9haejodelivery.dto;

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
public class OrderDeleteResponseDto {
  private UUID orderId;
  private OrderStatus status;
  private LocalDateTime deletedAt;
  private String message;

  public static OrderDeleteResponseDto of(
      UUID orderId,
      String message
  ) {
    return OrderDeleteResponseDto.builder()
                                 .orderId(orderId)
                                 .status(OrderStatus.ORDER_REJECTED)
                                 .deletedAt(LocalDateTime.now())
                                 .message(message)
                                 .build();
  }
}