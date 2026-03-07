package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.enums.PaymentStatus;

public record PaymentUpdateRequestDto(
        PaymentStatus status
) {}