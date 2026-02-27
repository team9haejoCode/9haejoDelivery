package com.sparta._9haejodelivery.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
  PAYMENT_PENDING("결제대기"),
  PAYMENT_COMPLETED("결제완료"),
  PAYMENT_FAILED("결제실패"),
  PAYMENT_CANCELED("결제취소"),
  REFUND_COMPLETED("환불완료");

  private final String description;
}
