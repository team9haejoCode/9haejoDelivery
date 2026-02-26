package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Review;
import com.sparta._9haejodelivery.dto.ReviewCreateRequestDTO;
import com.sparta._9haejodelivery.dto.ReviewResponseDTO;
import com.sparta._9haejodelivery.dto.ReviewUpdateDTO;
import com.sparta._9haejodelivery.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
//    private final UserRepository userRepository;
//    private final OrderRepository orderRepository;

    //todo: 생성, 조회, 매장별 전체조회, 수정, 삭제
    //생성 - ROLE=CUSTOMER 확인 -> 일단 OWNER만 차단하도록, ORDER 정보 추가  /todo: ROLE 확인
    public void createReview(ReviewCreateRequestDTO dto) {
        //토큰에서 ROLE 확인 및 유저 정보 획득
        //order 정보 확인 및 추가
        reviewRepository.save(Review.builder()
                .rating(new BigDecimal(dto.getRating()))
                .description(dto.getDescription()).build());//사용자 정보 추가
    }

    //조회 - body로 데이터 수신, COMMON: 전체 리뷰 확인 가능, CUSTOMER: 작성 리뷰 리스트 조회 가능, OWNER: ?
    //1. 전체 리뷰 조회
    public List<ReviewResponseDTO> findAllReviews() {
        List<ReviewResponseDTO> reviewList = new ArrayList<>();
        for(Review review:reviewRepository.findAll()){
            reviewList.add(ReviewResponseDTO.builder()
                    .reviewId(review.getReviewId())
                    .rating(review.getRating().toPlainString())
                    .description(review.getDescription())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt()
                    ).build());
        }
        return reviewList;
    }

    //2. 특정 리뷰 상세 조회(리뷰 ID 사용)
    public ReviewResponseDTO findReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new IllegalArgumentException("해당하는 리뷰를 찾을 수 없습니다."));
        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating().toPlainString())
                .description(review.getDescription())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt()
                ).build();
    }

    //3. 작성한 리뷰 조회 -> todo: 유저 및 토큰 연동 이후


    //4. 매장별 리뷰 조회 -> todo: order를 통해 확인한 판매점 정보 이용?
//    public List<ReviewResponseDTO> findReviewsByStoreId(UUID storeId) {
//        List<ReviewResponseDTO> reviewList = new ArrayList<>();
//        for(Review review:reviewRepository.findBy()){  //todo: 탐색방법 구상
//            reviewList.add(ReviewResponseDTO.builder()
//                    .reviewId(review.getReviewId())
//                    .rating(review.getRating())
//                    .description(review.getDescription())
//                    .createdAt(review.getCreatedAt())
//                    .updatedAt(review.getUpdatedAt()
//                    ).build());
//        }
//        return reviewList;
//    }

    //수정 - 작성자 확인
    public void updateReview(UUID reviewId, ReviewUpdateDTO dto) {
        Review review=reviewRepository.findById(reviewId)
                .orElseThrow(()->new IllegalArgumentException("해당하는 리뷰를 찾을 수 없습니다."));
        if(dto.rating!=null)
            review.setRating(new BigDecimal(dto.rating));
        if(dto.description!=null)
            review.setDescription(dto.description);
        reviewRepository.save(review);
    }

    //삭제 - 작성자 및 관리자권한 확인
    public void deleteReview(UUID reviewId) {
        Review review=reviewRepository.findById(reviewId)
                .orElseThrow(()->new IllegalArgumentException("해당하는 리뷰를 찾을 수 없습니다."));
        review.setIsHide(true);
        //review.markAsDeleted();  //todo: 삭제자 추가 방식 결정 후
    }

}
