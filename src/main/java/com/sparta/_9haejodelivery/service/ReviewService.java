package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.Review;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import com.sparta._9haejodelivery.dto.ReviewCreateRequestDto;
import com.sparta._9haejodelivery.dto.ReviewResponseDto;
import com.sparta._9haejodelivery.dto.ReviewUpdateDto;
import com.sparta._9haejodelivery.repository.ReviewRepository;
import com.sparta._9haejodelivery.repository.UserRepository;
import com.sparta._9haejodelivery.repository.temp_OrderRepository;
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
    //todo: 업데이트 후 임시 생성 저장소 사용 부분 수정
    private final /*UserService*/ UserRepository /*userService*/ userRepository;
    private final /*OrderService*/ temp_OrderRepository /*orderService*/ orderRepository;

    //todo: 이미 해당 주문에 대해 작성한 리뷰가 있는 경우 처리
    //생성 - ROLE=CUSTOMER 확인 -> 일단 OWNER만 차단하도록, ORDER 정보 추가  /todo: 보안 연동 후 수정
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public String createReview(ReviewCreateRequestDto dto, String username) {

        User user=/*userService*/ userRepository.findById(username)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        Order order=/*orderService*/ orderRepository.findById(dto.getOrderId())
                .orElseThrow(()->new IllegalArgumentException("해당 주문 내역을 찾을 수 없습니다."));

        //이미 해당 주문에 대해 작성한 리뷰가 있는 경우
        if(reviewRepository.findByOrder(order).isPresent())
            throw new IllegalStateException("이미 해당 주문에 대해 리뷰를 작성했습니다.");

        Review review = reviewRepository.save(Review.builder()
                .user(user)
                .order(order)
                .rating(new BigDecimal(dto.getRating()))
                .description(dto.getDescription()).build());//사용자 정보 추가

        return review.getReviewId().toString();
    }

    //조회 - body로 데이터 수신, ALL: 매장별 리뷰 확인 가능, CUSTOMER: 작성 리뷰 리스트 조회 가능,
    //1. 전체 리뷰 조회 - 관리자용
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
                .orElseThrow(()->new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating().toPlainString())
                .description(review.getDescription())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt()
                ).build();
    }

    //3. 작성한 리뷰 조회
    public Slice<ReviewResponseDto> findMyReviews(String username, Pageable pageable) {
        User user=/*userService*/ userRepository.findById(username)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        Slice<Review> reviewSlice = reviewRepository.findByUser(user, pageable);
        return reviewSlice.map(ReviewResponseDto::new);
    }

    //4. 매장별 리뷰 조회 -> todo: order를 통해 확인한 판매점 정보 이용?
    public Slice<ReviewResponseDto> findReviewsByStoreId(UUID storeId, Pageable pageable) {
        Slice<Review> reviewSlice = reviewRepository.findByStoreId(storeId, pageable);
        return reviewSlice.map(ReviewResponseDto::new);
    }

    //수정 - 작성자 확인
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER')")
    public void updateReview(UUID reviewId, ReviewUpdateDto dto, String username) throws AccessDeniedException {
        User user=/*userService*/ userRepository.findById(username)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));  //todo: user서비스 확인 후 수정
        Review review=reviewRepository.findById(reviewId)
                .orElseThrow(()->new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        if(!user.getUsername().equals(review.getUser().getUsername()))
            throw new AccessDeniedException("해당 리뷰의 작성자가 아닙니다.");

        if(dto.rating!=null)
            review.setRating(new BigDecimal(dto.rating));

        if(dto.description!=null)
            review.setDescription(dto.description);

        reviewRepository.save(review);
    }

    //삭제 - 작성자 및 관리자권한 확인
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER')")
    public void deleteReview(UUID reviewId, String username) throws AccessDeniedException {
        Review review=reviewRepository.findById(reviewId)
                .orElseThrow(()->new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        User user=userRepository.findById(username)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if(user.getRole()== UserRole.CUSTOMER && !username.equals(review.getUser().getUsername()))
            throw new AccessDeniedException("해당 권한이 없습니다.");

        review.setIsHide(true);
        review.markAsDeleted(username);
        reviewRepository.save(review);
    }

    //매장별 평점 조회용    todo: 매장별 평점 조회가능하도록 쿼리문 작성
    public String getStoreRating(UUID storeId) {
        return reviewRepository.getAverageRatingByStoreId(storeId).toString();
    }
}
