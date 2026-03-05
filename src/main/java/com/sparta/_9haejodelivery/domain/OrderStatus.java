package com.sparta._9haejodelivery.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
  ORDER_ACCEPTED(1, "주문접수"),
  ORDER_COMPLETED(2, "주문완료"),
  COOKING_DONE(3, "조리완료"),
  DELIVERING(4, "배달중"),
  DELIVERY_COMPLETED(5, "배달완료"),
  ORDER_REJECTED(0, "주문거절");

  private final int step;
  private final String description;

  public boolean isAfter(OrderStatus other) {
    // 0단계(거절)는 비교 대상에서 제외하거나 커스텀 로직 적용
    if (this.step == 0 || other.step == 0)
      return false;
    return this.step > other.step;
  }
}