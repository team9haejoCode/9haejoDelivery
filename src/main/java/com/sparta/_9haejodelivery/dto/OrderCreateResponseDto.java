package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreateResponseDto {

  private UUID orderId;
  private String username;
  private UUID storeId;
  private String storeName;
  private String status;
  private String address;
  private Integer totalPrice;
  private List<OrderItemResponseDto> orderItems;

  public static OrderCreateResponseDto from(Order order) {
    return OrderCreateResponseDto.builder()
                                 .orderId(order.getOrderId())
                                 .username(order.getUser().getUsername())
                                 .storeId(order.getStore().getStoreId())
                                 .storeName(order.getStore().getStoreName())
                                 .status(order.getStatus().name())
                                 .address(order.getAddress())
                                 .totalPrice(order.getTotalPrice())
                                 .orderItems(order.getOrderItemEntities()
                                                  .stream()
                                                  .map(OrderItemResponseDto::from)
                                                  .toList())
                                 .build();
  }
}