package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.ReviewCreateRequestDTO;
import com.sparta._9haejodelivery.dto.ReviewResponseDTO;
import com.sparta._9haejodelivery.dto.ReviewUpdateDTO;
import com.sparta._9haejodelivery.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
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
    public ApiResponse<String> createReview(@RequestBody ReviewCreateRequestDTO req,
                                                            @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        String reviewId=reviewService.createReview(req, userDetails.getUsername());
        return ApiResponse.success(HttpStatus.CREATED ,"리뷰가 작성되었습니다.",reviewId);
    }

    //get /reviews : 조회
    //get /stores/{storeId}/reviews : 매장 별 리뷰 조회  todo:reviews로 합치는 방식으로 우선 시도
    @Operation(summary = "리뷰 조회",
            description = "1. reviewId가 있으면 해당 리뷰 상세 조회, " +
                    "2. 매장 검색 기능 " +
                    "3. body에 값이 없으면 전체 조회")
    @GetMapping("/")
    public ApiResponse<?> getReviews(@RequestBody Map<String,Object> req) {
        if (req.containsKey("reviewId")) {
            return ApiResponse.success(HttpStatus.OK ,"리뷰 조회 성공", reviewService
                            .findReviewById(UUID
                                    .fromString(req.get("reviewId").toString())));
        }else if(req.containsKey("storeId")){
            return ApiResponse.success(HttpStatus.OK,
                            "리뷰 조회 성공",
                            reviewService.findReviewsByStoreId(
                                    UUID.fromString(req.get("storeId").toString())));
        } else{ //전체 리뷰 조회는 관리자 권한 확인 후 진행
            return ApiResponse.success(HttpStatus.OK,
                            "리뷰 조회 성공",
                            reviewService.findAllReviews());
        }
    }

    @Operation(summary = "작성한 리뷰 조회")
    @GetMapping("/myReviews")
    public ApiResponse<List<ReviewResponseDTO>> getMyReviews(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(HttpStatus.OK,
                        "리뷰 조회 성공",
                        reviewService.findMyReviews(userDetails.getUsername()));
    }

    //patch /reviews/{reviewId} : 수정
    @Operation(summary = "리뷰 수정")
    @PatchMapping("/{reviewId}")
    public ApiResponse<String> updateReview(@PathVariable String reviewId,
                                                            @RequestBody ReviewUpdateDTO req,
                                                            @AuthenticationPrincipal UserDetails userDetails)
            throws AccessDeniedException {
        reviewService.updateReview(UUID
                        .fromString(reviewId)
                , req, userDetails.getUsername());
        return ApiResponse.success(HttpStatus.OK,
                        "리뷰가 수정되었습니다.",reviewId);
    }

    //delete /reviews/{reviewId} : 소프트 삭제
    @Operation(summary = "리뷰 삭제", description = "소프트 삭제")
    @DeleteMapping("/{reviewId}")
    public ApiResponse<String> deleteReview(@PathVariable UUID reviewId,
                                                            @AuthenticationPrincipal UserDetails userDetails)
            throws AccessDeniedException {
        reviewService.deleteReview(reviewId,userDetails.getUsername());
        return ApiResponse.success(HttpStatus.OK,"리뷰가 삭제되었습니다.");
    }
}