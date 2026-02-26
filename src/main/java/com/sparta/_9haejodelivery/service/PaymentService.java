package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Payment;
import com.sparta._9haejodelivery.domain.PaymentHistory; // 추가
import com.sparta._9haejodelivery.domain.enums.PaymentStatus;
import com.sparta._9haejodelivery.dto.request.PaymentRequestDto;
import com.sparta._9haejodelivery.dto.request.PaymentUpdateRequestDto; // 추가
import com.sparta._9haejodelivery.dto.response.PaymentResponseDto;
import com.sparta._9haejodelivery.repository.PaymentHistoryRepository; // 추가
import com.sparta._9haejodelivery.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {
        Payment payment = Payment.builder()
                .orderId(requestDto.orderId())
                .amount(requestDto.amount())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentResponseDto.from(savedPayment);
    }


    @Transactional
    public PaymentResponseDto updatePaymentStatus(Long paymentId, PaymentUpdateRequestDto requestDto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 내역을 찾을 수 없습니다. ID: " + paymentId));

        payment.updateStatus(requestDto.status());

        if (requestDto.status() == PaymentStatus.COMPLETED) {
            String fakePgId = "toss_mock_" + UUID.randomUUID().toString().substring(0, 8);
            payment.completePayment(fakePgId);
        }

        PaymentHistory history = PaymentHistory.builder()
                .payment(payment)
                .status(payment.getStatus())
                .build();
        paymentHistoryRepository.save(history);
        
        return PaymentResponseDto.from(payment);
    }
}