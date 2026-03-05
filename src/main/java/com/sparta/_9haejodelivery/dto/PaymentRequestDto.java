package com.sparta._9haejodelivery.dto;

import java.util.UUID;

public record PaymentRequestDto(
        UUID orderId,
        int amount
) {}