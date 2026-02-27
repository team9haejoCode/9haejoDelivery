package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderUpdateRequestDto {
  private OrderStatus status;
  @Length(max = 100)
  private String address;
}