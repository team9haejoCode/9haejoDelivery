package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.OrderItem;
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
public class OrderItemResponseDto {
  private UUID productId;
  private String productName;
  private String productImageUrl;
  private Integer quantity;
  private Integer unitPrice;
  private Integer subTotal;

  public static OrderItemResponseDto from(OrderItem item) {
    return OrderItemResponseDto.builder()
                               .productId(item.getProduct().getProductId())
                               .productName(item.getProduct().getProductName())
                               .productImageUrl(item.getProduct().getImage())
                               .quantity(item.getQuantity())
                               .unitPrice(item.getUnitPrice())
                               .subTotal(item.getSubTotal())
                               .build();
  }
}
