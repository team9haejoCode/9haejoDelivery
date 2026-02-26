package com.sparta._9haejodelivery.dto.request;

import com.sparta._9haejodelivery.domain.enums.PaymentStatus;

public record PaymentUpdateRequestDto(
        PaymentStatus status
) {}