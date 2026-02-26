package com.sparta._9haejodelivery.dto.response;

import com.sparta._9haejodelivery.domain.Payment;
import com.sparta._9haejodelivery.domain.enums.PaymentStatus;

public record PaymentResponseDto(
        Long paymentId,
        Long orderId,
        Long amount,
        PaymentStatus status
) {
    public static PaymentResponseDto from(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}