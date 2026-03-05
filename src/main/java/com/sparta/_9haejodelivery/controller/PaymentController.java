package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.PaymentRequestDto;
import com.sparta._9haejodelivery.dto.PaymentResponseDto;
import com.sparta._9haejodelivery.dto.PaymentUpdateRequestDto;
import com.sparta._9haejodelivery.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 1. 결제 생성 API (POST)
    @PostMapping
    public ApiResponse<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto requestDto) {
        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);
        return ApiResponse.success(HttpStatus.CREATED, "결제 생성 성공", responseDto);
    }

    // 2. 결제 단건 조회 API (GET)
    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentResponseDto> getPayment(@PathVariable("paymentId") UUID paymentId) {
        PaymentResponseDto responseDto = paymentService.getPayment(paymentId);
        return ApiResponse.success(HttpStatus.OK, "결제 단건 조회 성공", responseDto);
    }

    // 3. 결제 목록 조회 API (GET)
    @GetMapping
public ApiResponse<Page<PaymentResponseDto>> getPaymentList(
        @RequestParam(value = "orderId", required = false) UUID orderId,
        Pageable pageable) { // <-- 여기에 Pageable 추가!

    Page<PaymentResponseDto> responsePage = paymentService.getPaymentList(orderId, pageable);
    return ApiResponse.success(HttpStatus.OK, "결제 목록 조회 성공", responsePage);
}

    // 4. 결제 상태 변경 API (PATCH)
    @PatchMapping("/{paymentId}")
    public ApiResponse<PaymentResponseDto> updatePaymentStatus(
            @PathVariable("paymentId") UUID paymentId,
            @RequestBody PaymentUpdateRequestDto requestDto) {
        PaymentResponseDto responseDto = paymentService.updatePaymentStatus(paymentId, requestDto);
        return ApiResponse.success(HttpStatus.OK, "결제 상태 변경 성공", responseDto);
    }

    // 5. 결제 삭제 API (DELETE)
    @DeleteMapping("/{paymentId}")
    public ApiResponse<Void> deletePayment(@PathVariable("paymentId") UUID paymentId) {
        paymentService.deletePayment(paymentId, null);
        return ApiResponse.success(HttpStatus.OK, "요청이 정상 처리되었습니다.");
    }
}