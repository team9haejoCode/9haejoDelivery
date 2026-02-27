package com.sparta._9haejodelivery.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
  ORDER_ACCEPTED("주문접수"),
  ORDER_REJECTED("주문거절"),
  ORDER_COMPLETED("주문완료"),
  COOKING_DONE("조리완료"),
  DELIVERING("배달중"),
  DELIVERY_COMPLETED("배달완료");

  private final String description;
}