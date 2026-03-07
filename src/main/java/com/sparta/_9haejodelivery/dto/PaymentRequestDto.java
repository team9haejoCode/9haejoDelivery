package com.sparta._9haejodelivery.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(
        @NotNull
        UUID orderId,
        @Min(value = 0, message = "금액은 0 이상이어야 합니다.")
        int amount
) {}