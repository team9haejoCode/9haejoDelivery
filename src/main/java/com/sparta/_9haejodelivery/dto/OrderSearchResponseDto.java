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
public class OrderSearchResponseDto {
  private UUID orderId;
  private String username;
  private String nickname;
  private UUID storeId;
  private String storeName;
  private OrderStatus status;
  private Integer totalPrice;
  private String address;
  private String orderSummary;
  private LocalDateTime createdAt;

  public static OrderSearchResponseDto from(
      Order order
  ) {
    return OrderSearchResponseDto.builder()
                                 .orderId(order.getOrderId())
                                 .username(order.getUser() // 일시적으로 Id만 갖는 프록시 객체로 N+1 발생 없음
                                                .getUsername())
                                 .nickname(order.getUser().getNickname())
                                 .storeId(order.getStore().getStoreId())
                                 .storeName(order.getStore().getStoreName())
                                 .status(order.getStatus())
                                 .totalPrice(order.getTotalPrice())
                                 .address(order.getAddress())
                                 .orderSummary(order.getOrderSummary())
                                 .createdAt(order.getCreatedAt())
                                 .build();
  }
}