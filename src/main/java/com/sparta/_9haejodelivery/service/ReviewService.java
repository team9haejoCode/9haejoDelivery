package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.common.BusinessException;
import com.sparta._9haejodelivery.common.ErrorCode;
import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.Review;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import com.sparta._9haejodelivery.dto.ReviewCreateRequestDto;
import com.sparta._9haejodelivery.dto.ReviewResponseDto;
import com.sparta._9haejodelivery.dto.ReviewUpdateDto;
import com.sparta._9haejodelivery.repository.OrderRepository;
import com.sparta._9haejodelivery.repository.ReviewRepository;
import com.sparta._9haejodelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

//todo: 현재 리뷰 제외 엔티티들 임의 수정버전, 수정 시 확인
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    private final  UserRepository  userRepository;
    private final  OrderRepository  orderRepository;

    //생성 -OWNER만 차단
    @PreAuthorize("!hasRole('OWNER')")
    public String createReview(ReviewCreateRequestDto dto, String username) {

        User user= userRepository.findById(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));
        Order order= orderRepository.findById(dto.getOrderId())
                .orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        //이미 해당 주문에 대해 작성한 리뷰가 있는 경우
        if(reviewRepository.findByOrder(order).isPresent())
            throw new BusinessException(ErrorCode.ALREADY_EXIST_REVIEW);

        Review review = reviewRepository.save(Review.builder()
                .user(user)
                .order(order)
                .rating(new BigDecimal(dto.getRating()))
                .description(dto.getDescription()).build());//사용자 정보 추가

        return review.getReviewId().toString();
    }

    /*
    * 조회
    * */
    //1. 전체 리뷰 조회 - 관리자용
    @PreAuthorize("hasAnyRole('MANAGER','MASTER')")
    public Page<ReviewResponseDto> findAllReviews(Pageable pageable) {
        Page<Review>reviewPage=reviewRepository.findAll(pageable);
        return reviewPage.map(review -> ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating().toPlainString())
                .description(review.getDescription())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt()
                ).build());
    }

    //2. 리뷰 상세 조회(리뷰 ID 사용)
    public ReviewResponseDto findReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating().toPlainString())
                .description(review.getDescription())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt()
                ).build();
    }

    //3. 작성한 리뷰 조회
    @PreAuthorize("!hasRole('OWNER')")
    public Slice<ReviewResponseDto> findMyReviews(String username, Pageable pageable) {
        User user= userRepository.findById(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));
        Slice<Review> reviewSlice = reviewRepository.findByUser(user, pageable);
        return reviewSlice.map(ReviewResponseDto::new);
    }

    //4. 매장별 리뷰 조회
    public Slice<ReviewResponseDto> findReviewsByStoreId(UUID storeId, Pageable pageable) {
        Slice<Review> reviewSlice = reviewRepository.findByStoreId(storeId, pageable);
        return reviewSlice.map(ReviewResponseDto::new);
    }

    //수정 - 작성자 확인
    @PreAuthorize("!hasRole('OWNER')")
    public void updateReview(UUID reviewId, ReviewUpdateDto dto, String username) throws AccessDeniedException {
        User user= userRepository.findById(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));
        Review review=reviewRepository.findById(reviewId)
                .orElseThrow(()->new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if(!user.getUsername().equals(review.getUser().getUsername()))
            throw new BusinessException(ErrorCode.NOT_AUTHOR);

        if(dto.rating!=null)
            review.setRating(new BigDecimal(dto.rating));

        if(dto.description!=null)
            review.setDescription(dto.description);

        reviewRepository.save(review);
    }

    //삭제 - 작성자 및 관리자권한 확인
    @PreAuthorize("!hasRole('OWNER')")
    public void deleteReview(UUID reviewId, String username) throws AccessDeniedException {
        Review review=reviewRepository.findById(reviewId)
                .orElseThrow(()->new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        User user=userRepository.findById(username)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(user.getRole()== UserRole.CUSTOMER && !username.equals(review.getUser().getUsername()))
            throw new BusinessException(ErrorCode.NOT_AUTHORIZED);

        review.setIsHide(true);
        review.markAsDeleted(username);
        reviewRepository.save(review);
    }
}
