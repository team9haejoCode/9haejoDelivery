package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.dto.ReviewCreateRequestDTO;
import com.sparta._9haejodelivery.dto.ReviewResponseDTO;
import com.sparta._9haejodelivery.dto.ReviewUpdateDTO;
import com.sparta._9haejodelivery.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createReview(@RequestBody ReviewCreateRequestDTO req) throws AccessDeniedException {
        UserDetails userDetails=null;//todo: 보안부분 연동 후 details 가져오도록 수정
        reviewService.createReview(req, userDetails.getUsername());
        return ResponseEntity.ok().body("리뷰가 작성되었습니다.");
    }

    //get /reviews : 조회, {reviewId}는 body로 포함, 없으면 전체 출력?
    //get /stores/{storeId}/reviews : 매장 별 리뷰 조회  todo:reviews로 합치는 방식으로 우선 시도
    @Operation(summary = "리뷰 조회",
            description = "1. reviewId가 있으면 해당 리뷰 상세 조회, " +
                    "2. 매장 검색 기능 - 현재 비활성화, " +
                    "3. body에 값이 없으면 전체 조회")
    @GetMapping("/")
    public ResponseEntity<?> getReviews(@RequestBody Map<String,Object> req) {
        UserDetails userDetails=null;
        if (req.containsKey("reviewId")) {
            return ResponseEntity.ok()
                    .body(reviewService
                            .findReviewById(UUID
                                    .fromString(req.get("reviewId").toString())));
        }else if(req.containsKey("storeId")){
            return ResponseEntity.ok().body(
                    reviewService.findReviewsByStoreId(
                            UUID.fromString(req.get("storeId").toString())));
        } else{ //전체 리뷰 조회는 관리자 권한 확인 후 진행
            return ResponseEntity.ok().body(reviewService.findAllReviews());
        }
    }

    @Operation(summary = "작성한 리뷰 조회")
    @GetMapping("/myReviews")
    public ResponseEntity<List<ReviewResponseDTO>> getMyReviews() {
        UserDetails userDetails=null; //todo: 보안연동 후 수정
        return ResponseEntity.ok().body(reviewService.findMyReviews(userDetails.getUsername()));
    }

    //patch /reviews/{reviewId} : 수정
    @Operation(summary = "리뷰 수정")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable UUID reviewId,
                                               @RequestBody ReviewUpdateDTO req)
            throws AccessDeniedException {
        UserDetails userDetails=null;   //todo: 보안연동 후 수정
        reviewService.updateReview(reviewId, req, userDetails.getUsername());
        return ResponseEntity.ok().body("리뷰가 수정되었습니다.");
    }

    //delete /reviews/{reviewId} : 소프트 삭제
    @Operation(summary = "리뷰 삭제", description = "소프트 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID reviewId)
            throws AccessDeniedException {
        UserDetails userDetails=null;   //todo: 보안연동 후 수정
        reviewService.deleteReview(reviewId,userDetails.getUsername());
        return ResponseEntity.ok().body("리뷰가 삭제되었습니다.");
    }
}