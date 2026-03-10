package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.ReviewCreateRequestDto;
import com.sparta._9haejodelivery.dto.ReviewResponseDto;
import com.sparta._9haejodelivery.dto.ReviewUpdateDto;
import com.sparta._9haejodelivery.global.security.UserDetailsImpl;
import com.sparta._9haejodelivery.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

//todo: 반환타입 확인, 현재 테스트를 위해 필요한 부분 직접 받는 방식으로 임시 작성 예정-> 수정 예정
//todo: 현재 테스트를 위해 필요한 부분 직접 받는 방식으로 임시 작성 예정 - details 쿼리 파라미터로 입력받는 방향
@Tag(name = "Review",description="리뷰관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    //post /reviews: 생성
    @Operation(summary = "리뷰 작성")
    @PostMapping("/")
    public ResponseEntity<ApiResponse<String>> createReview(@RequestBody ReviewCreateRequestDto req,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        String reviewId=reviewService.createReview(req, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED ,"리뷰가 작성되었습니다.",reviewId));
    }

    //get /reviews : 조회
    //get /stores/{storeId}/reviews : 매장 별 리뷰 조회  todo:reviews로 합치는 방식으로 우선 시도
    @Operation(summary = "리뷰 상세 조회")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> getReviewByReviewId(@PathVariable String reviewId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK ,"리뷰 조회 성공", reviewService
                            .findReviewById(UUID
                                    .fromString(reviewId))));
    }

    @Operation(summary = "매장별 리뷰 조회")
    @GetMapping("/reviews/{storeId}")
    public ResponseEntity<ApiResponse<Slice<ReviewResponseDto>>> getReviewsByStoreId(@PathVariable String storeId,
                                                                                    @PageableDefault
                                                                             (size = 10,
                                                                                     sort = "createdAt",
                                                                                     direction = Sort.Direction.DESC) Pageable pageable) {
        //pagable 검증 : 10, 30, 50건 외의 값은 10으로 고정
        if(pageable.getPageSize()!=10 && pageable.getPageSize()!=30 && pageable.getPageSize()!=50){
            pageable= PageRequest.of(pageable.getPageNumber(),10,pageable.getSort());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK ,"리뷰 조회 성공", reviewService
                        .findReviewsByStoreId(UUID
                                .fromString(storeId),pageable)));
    }

    @Operation(summary = "리뷰 전체 조회")
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponseDto>>> getAllReviews(@PageableDefault
                                                                                             (size = 10,
                                                                                                     sort = "createdAt",
                                                                                                     direction = Sort.Direction.DESC) Pageable pageable) {
        //pagable 검증 : 10, 30, 50건 외의 값은 10으로 고정
        if (pageable.getPageSize() != 10 && pageable.getPageSize() != 30 && pageable.getPageSize() != 50) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "리뷰 조회 성공", reviewService
                        .findAllReviews(pageable)));
    }

    @Operation(summary = "작성한 리뷰 조회")
    @GetMapping("/myReviews")
    public ResponseEntity<ApiResponse<Slice<ReviewResponseDto>>> getMyReviews(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PageableDefault
                                                                     (size = 10,
                                                                             sort = "createdAt",
                                                                             direction = Sort.Direction.DESC) Pageable pageable) {
        //pagable 검증 : 10, 30, 50건 외의 값은 10으로 고정
        if(pageable.getPageSize()!=10 && pageable.getPageSize()!=30 && pageable.getPageSize()!=50){
            pageable= PageRequest.of(pageable.getPageNumber(),10,pageable.getSort());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,
                        "리뷰 조회 성공",
                        reviewService.findMyReviews(userDetails.getUsername(),pageable)));
    }

    //patch /reviews/{reviewId} : 수정
    @Operation(summary = "리뷰 수정")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> updateReview(@PathVariable String reviewId,
                                                            @RequestBody ReviewUpdateDto req,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws AccessDeniedException {
        reviewService.updateReview(UUID
                        .fromString(reviewId)
                , req, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,
                        "리뷰가 수정되었습니다.",reviewId));
    }

    //delete /reviews/{reviewId} : 소프트 삭제
    @Operation(summary = "리뷰 삭제", description = "소프트 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable UUID reviewId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws AccessDeniedException {
        reviewService.deleteReview(reviewId,userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"리뷰가 삭제되었습니다."));
    }
}