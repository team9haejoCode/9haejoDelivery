package com.sparta._9haejodelivery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta._9haejodelivery.domain.Payment;
import com.sparta._9haejodelivery.domain.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDto(
        UUID paymentId, 
        UUID orderId,
        int amount,
        PaymentStatus status,
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
    public static PaymentResponseDto from(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}