package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Payment;
import com.sparta._9haejodelivery.domain.PaymentHistory; 
import com.sparta._9haejodelivery.domain.enums.PaymentStatus;
import com.sparta._9haejodelivery.dto.PaymentRequestDto;
import com.sparta._9haejodelivery.dto.PaymentResponseDto;
import com.sparta._9haejodelivery.dto.PaymentUpdateRequestDto;
import com.sparta._9haejodelivery.repository.PaymentHistoryRepository; 
import com.sparta._9haejodelivery.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        PaymentHistory history = PaymentHistory.builder()
                .payment(savedPayment)
                .orderId(savedPayment.getOrderId())
                .previousStatus(null)
                .currentStatus(savedPayment.getStatus())
                .amount(savedPayment.getAmount())
                .significant("결제 최초 생성")
                .build();
        paymentHistoryRepository.save(history);

        return PaymentResponseDto.from(savedPayment);
    }

    @Transactional
    public PaymentResponseDto updatePaymentStatus(Long paymentId, PaymentUpdateRequestDto requestDto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 내역을 찾을 수 없습니다. ID: " + paymentId));

        PaymentStatus previousStatus = payment.getStatus();

        payment.updateStatus(requestDto.status());

        if (requestDto.status() == PaymentStatus.COMPLETED) {
            String fakePgId = "toss_mock_" + UUID.randomUUID().toString().substring(0, 8);
            payment.completePayment(fakePgId);
        }

        PaymentHistory history = PaymentHistory.builder()
                .payment(payment)
                .orderId(payment.getOrderId())
                .previousStatus(previousStatus)
                .currentStatus(payment.getStatus())
                .amount(payment.getAmount())
                .significant("결제 상태 업데이트")
                .build();
        paymentHistoryRepository.save(history);

        return PaymentResponseDto.from(payment);
    }

    // 1. 단건 조회 (GET)
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다. ID: " + paymentId));
        return PaymentResponseDto.from(payment);
    }

    // 2. 다건 조회 (GET)
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentList(Long orderId) {
        List<Payment> payments;
        
        if (orderId != null) {
            payments = paymentRepository.findAllByOrderId(orderId);
        } else {
            payments = paymentRepository.findAll();
        }

        return payments.stream()
                .map(PaymentResponseDto::from)
                .toList();
    }

    // 3. 결제 삭제/취소 (DELETE)
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다. ID: " + paymentId));

        PaymentStatus previousStatus = payment.getStatus();

        payment.cancelPayment(); 

        PaymentHistory history = PaymentHistory.builder()
                .payment(payment)
                .orderId(payment.getOrderId())
                .previousStatus(previousStatus)
                .currentStatus(payment.getStatus()) // CANCELED
                .amount(payment.getAmount())
                .significant("결제 삭제 (취소 처리)")
                .build();
        paymentHistoryRepository.save(history);
    }
}