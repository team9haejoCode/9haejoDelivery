package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.dto.request.PaymentRequestDto;
import com.sparta._9haejodelivery.dto.request.PaymentUpdateRequestDto;
import com.sparta._9haejodelivery.dto.response.PaymentResponseDto;
import com.sparta._9haejodelivery.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Helper method to guarantee JSON key order
    private Map<String, Object> createResponse(String code, String status, String message, Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", code);
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    // 1. 결제 생성 API (POST)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody PaymentRequestDto requestDto) {
        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);
        return ResponseEntity.ok(createResponse("200", "OK", "결제 생성 성공", responseDto));
    }

    // 2. 결제 단건 조회 API (GET)
    @GetMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPayment(@PathVariable("paymentId") Long paymentId) {
        PaymentResponseDto responseDto = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(createResponse("200", "OK", "결제 단건 조회 성공", responseDto));
    }

    // 3. 결제 목록 조회 API (GET)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPaymentList(
            @RequestParam(value = "orderId", required = false) Long orderId) {
        List<PaymentResponseDto> responseList = paymentService.getPaymentList(orderId);
        return ResponseEntity.ok(createResponse("200", "OK", "결제 목록 조회 성공", responseList));
    }

    // 4. 결제 상태 변경 API (PATCH)
    @PatchMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentUpdateRequestDto requestDto) {
        PaymentResponseDto responseDto = paymentService.updatePaymentStatus(paymentId, requestDto);
        return ResponseEntity.ok(createResponse("200", "OK", "결제 상태 변경 성공", responseDto));
    }

    // 5. 결제 삭제 API (DELETE)
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> deletePayment(@PathVariable("paymentId") Long paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok(createResponse("200", "OK", "요청이 정상 처리되었습니다.", null));
    }
}