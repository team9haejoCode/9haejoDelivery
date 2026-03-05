package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderUpdateResponseDto {
  @NotNull
  private UUID orderId;
  @NotNull
  private String username;
  @Length(max = 100)
  private String address;
  private OrderStatus status;

  public static OrderUpdateResponseDto from(
      Order order,
      String username
  ) {
    return OrderUpdateResponseDto.builder()
                                 .orderId(order.getOrderId())
                                 .status(order.getStatus())
                                 .address(order.getAddress())
                                 .username(username)
                                 .build();
  }
}