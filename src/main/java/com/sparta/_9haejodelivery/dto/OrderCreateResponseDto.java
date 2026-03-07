package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.OrderItem;
import com.sparta._9haejodelivery.domain.Product;
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
  private String status;
  private String address;
  private Integer totalPrice;
  private List<OrderItemResponseDto> orderItems;

  /**
   * Entity -> DTO 변환을 위한 정적 팩토리 메서드
   */
  public static OrderCreateResponseDto from(Order order) {
    return OrderCreateResponseDto.builder()
                                 .orderId(order.getOrderId())
                                 .username(order.getUser()
                                                .getUsername())
                                 .status(order.getStatus()
                                              .name())
                                 .address(order.getAddress())
                                 .totalPrice(order.getTotalPrice())
                                 .orderItems(order.getOrderItemEntities()
                                                  .stream()
                                                  .map(OrderItemResponseDto::from)
                                                  .toList())
                                 .build();
  }

  /**
   * 내부 클래스: 주문 상세 아이템 응답용
   */
  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class OrderItemResponseDto {
    private Product product;
    private Integer quantity;
    private Integer unitPrice;
    private Integer subTotal;

    public static OrderItemResponseDto from(OrderItem item) {
      return OrderItemResponseDto.builder()
                                 .product(item.getProduct())
                                 .quantity(item.getQuantity())
                                 .unitPrice(item.getUnitPrice())
                                 .subTotal(item.getSubTotal())
                                 .build();
    }
  }
}