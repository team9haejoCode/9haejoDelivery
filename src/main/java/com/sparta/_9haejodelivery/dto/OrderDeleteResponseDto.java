package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Order;
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
  private String orderOwnerName;
  private String deletedBy;
  private LocalDateTime deletedAt;
  private String message;

  public static OrderDeleteResponseDto of(Order order, String executorName, String message) {
    return OrderDeleteResponseDto.builder()
                                 .orderId(order.getOrderId())
                                 .orderOwnerName(order.getUser().getUsername())
                                 .deletedBy(executorName)
                                 .deletedAt(order.getDeletedAt())
                                 .message(message)
                                 .build();
  }
}