package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.dto.ReviewCreateRequestDTO;
import com.sparta._9haejodelivery.dto.ReviewUpdateDTO;
import com.sparta._9haejodelivery.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Review",description="리뷰관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    //todo: 반환타입 확인
    //post /reviews : 생성
    @Operation(summary = "리뷰 작성")
    @PostMapping("/")
    public ResponseEntity<String> createReview(@RequestBody ReviewCreateRequestDTO req) {
        reviewService.createReview(req);
        return ResponseEntity.ok().body("리뷰가 작성되었습니다.");
    }

    //get /reviews : 조회, {reviewId}는 body로 포함, 없으면 전체 출력?
    //get /stores/{storeId}/reviews : 매장 별 리뷰 조회  todo:reviews로 합치는 방식으로 우선 시도
    @Operation(summary = "리뷰 조회",
            description = "1. body에 값이 없으면 전체 조회, " +
                    "2. reviewId가 있으면 해당 리뷰 상세 조회, " +
                    "3. 매장 및 작성자 검색 기능 추가 예정")
    @GetMapping("/")
    public ResponseEntity<?> getReviews(@RequestBody Map<String,Object> req) {
        if (req.containsKey("reviewId")) {
            return ResponseEntity.ok()
                    .body(reviewService
                            .findReviewById(UUID
                                    .fromString(req.get("reviewId").toString())));
        }
        //else if 로 작성한 리뷰 및 매장별 리뷰 조회 추가
        else{
            return ResponseEntity.ok().body(reviewService.findAllReviews());
        }
    }

    //patch /reviews/{reviewId} : 수정
    @Operation(summary = "리뷰 수정")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable UUID reviewId, @RequestBody ReviewUpdateDTO req) {
        reviewService.updateReview(reviewId, req);
        return ResponseEntity.ok().body("리뷰가 수정되었습니다.");
    }

    //delete /reviews/{reviewId} : 소프트 삭제
    @Operation(summary = "리뷰 삭제", description = "소프트 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().body("리뷰가 삭제되었습니다.");
    }
}