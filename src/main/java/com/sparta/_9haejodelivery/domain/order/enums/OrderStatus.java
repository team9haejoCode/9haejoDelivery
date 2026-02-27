package com.sparta._9haejodelivery.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
  ORDER_ACCEPTED("주문접수"),
  ORDER_REJECTED("주문거절"),
  COOKING_DONE("조리완료"),
  DELIVERING("배송중"),
  DELIVERY_COMPLETED("배송완료"),
  ORDER_COMPLETED("주문완료");

  private final String description;
}