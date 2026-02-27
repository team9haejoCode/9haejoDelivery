package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.PaymentEntity;
import com.sparta._9haejodelivery.domain.PaymentHistoryEntity; // 추가
import com.sparta._9haejodelivery.domain.enums.PaymentStatus;
import com.sparta._9haejodelivery.dto.request.PaymentRequestDto;
import com.sparta._9haejodelivery.dto.request.PaymentUpdateRequestDto; // 추가
import com.sparta._9haejodelivery.dto.response.PaymentResponseDto;
import com.sparta._9haejodelivery.repository.PaymentHistoryRepository; // 추가
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
        PaymentEntity payment = PaymentEntity.builder()
                .orderId(requestDto.orderId())
                .amount(requestDto.amount())
                .status(PaymentStatus.PENDING)
                .build();

        PaymentEntity savedPayment = paymentRepository.save(payment);
        return PaymentResponseDto.from(savedPayment);
    }


    @Transactional
    public PaymentResponseDto updatePaymentStatus(Long paymentId, PaymentUpdateRequestDto requestDto) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 내역을 찾을 수 없습니다. ID: " + paymentId));

        payment.updateStatus(requestDto.status());

        if (requestDto.status() == PaymentStatus.COMPLETED) {
            String fakePgId = "toss_mock_" + UUID.randomUUID().toString().substring(0, 8);
            payment.completePayment(fakePgId);
        }

        PaymentHistoryEntity history = PaymentHistoryEntity.builder()
                .payment(payment)
                .status(payment.getStatus())
                .build();
        paymentHistoryRepository.save(history);

        return PaymentResponseDto.from(payment);
    }

    // 1. 단건 조회 (GET)
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다. ID: " + paymentId));
        return PaymentResponseDto.from(payment);
    }

    // 2. 다건 조회 (GET)
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentList(Long orderId) {
        List<PaymentEntity> payments;
        
        // orderId 파라미터가 있으면 필터링, 없으면 전체 조회
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
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다. ID: " + paymentId));

        // Security를 꺼둔 상태이므로 임시로 "SYSTEM"이라는 이름을 넘겨줍니다.
        // 나중에 @AuthenticationPrincipal을 통해 실제 유저 이름을 받아와야 합니다.
        payment.cancelPayment("SYSTEM"); 

        // 취소 상태도 히스토리에 기록
        PaymentHistoryEntity history = PaymentHistoryEntity.builder()
                .payment(payment)
                .status(payment.getStatus())
                .build();
        paymentHistoryRepository.save(history);
    }
}