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
  ORDER_REJECTED(0, "주문거절"),
  ORDER_CANCELED(-1, "주문취소");

  private final int step;
  private final String description;

  public boolean isAfter(OrderStatus other) {
    // 현재 내 상태가 이미 최종(거절/취소/완료)이면 전이 불가능 (엔티티에서 이미 체크하지만 이중 방어)
    if (this == ORDER_REJECTED || this == ORDER_CANCELED || this == DELIVERY_COMPLETED) {
      return true;
    }

    // 정상 흐름 간의 비교
    if (this.step > 0 && other.step > 0) {
      return this.step > other.step;
    }

    return false;
  }
}