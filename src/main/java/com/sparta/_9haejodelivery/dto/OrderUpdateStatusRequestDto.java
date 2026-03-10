package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderUpdateStatusRequestDto {
  @NotNull(message = "변경할 주문 상태는 필수 입력값입니다.")
  private OrderStatus status;
}
