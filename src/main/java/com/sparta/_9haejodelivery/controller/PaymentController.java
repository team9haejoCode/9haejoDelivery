package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.dto.request.PaymentRequestDto;
import com.sparta._9haejodelivery.dto.request.PaymentUpdateRequestDto;
import com.sparta._9haejodelivery.dto.response.PaymentResponseDto;
import com.sparta._9haejodelivery.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 생성 API (POST)
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto requestDto) {
        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 결제 상태 변경 API (PATCH)
    @PatchMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> updatePaymentStatus(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentUpdateRequestDto requestDto) {
            
        PaymentResponseDto responseDto = paymentService.updatePaymentStatus(paymentId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
}