package com.sparta._9haejodelivery.dto;

public record PaymentRequestDto(
        Long orderId,
        Long amount
) {}