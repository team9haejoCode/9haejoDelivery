package com.sparta._9haejodelivery.dto.request;

public record PaymentRequestDto(
        Long orderId,
        Long amount
) {}