package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Review;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.dto.ReviewCreateRequestDTO;
import com.sparta._9haejodelivery.dto.ReviewResponseDTO;
import com.sparta._9haejodelivery.dto.ReviewUpdateDTO;
import com.sparta._9haejodelivery.repository.OrderRepository;
import com.sparta._9haejodelivery.repository.ReviewRepository;
import com.sparta._9haejodelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//todo: 현재 리뷰 제외 엔티티들 임의 수정버전, 수정 시 확인
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    //todo: 업데이트 후 임시 생성 저장소 사용 부분 수정
    private final /*UserService*/ UserRepository /*userService*/ userRepository;
    private final /*OrderService*/ OrderRepository /*orderService*/ orderRepository;

    //todo: 이미 해당 주문에 대해 작성한 리뷰가 있는 경우 처리
    //생성 - ROLE=CUSTOMER 확인 -> 일단 OWNER만 차단하도록, ORDER 정보 추가  /todo: 보안 연동 후 수정
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public void createReview(ReviewCreateRequestDTO dto, String username) throws AccessDeniedException {

        User user=/*userService*/ userRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        Order order=/*orderService*/ orderRepository.findById(dto.getOrderId())
                .orElseThrow(()->new IllegalArgumentException("해당 주문 내역을 찾을 수 없습니다."));

        //이미 해당 주문에 대해 작성한 리뷰가 있는 경우
        if(reviewRepository.findByOrder(order)!=null)
            throw new IllegalStateException("이미 해당 주문에 대해 리뷰를 작성했습니다.");

        reviewRepository.save(Review.builder()
                .user(user)
                .order(order)
                .rating(new BigDecimal(dto.getRating()))
                .description(dto.getDescription()).build());//사용자 정보 추가
    }

    //조회 - body로 데이터 수신, ALL: 매장별 리뷰 확인 가능, CUSTOMER: 작성 리뷰 리스트 조회 가능,
    //1. 전체 리뷰 조회 - 관리자용
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    //2. 리뷰 상세 조회(리뷰 ID 사용)
    public ReviewResponseDTO findReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating().toPlainString())
                .description(review.getDescription())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt()
                ).build();
    }

    //3. 작성한 리뷰 조회
    public List<ReviewResponseDTO> findMyReviews(String username) {
        User user=/*userService*/ userRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        List<ReviewResponseDTO> reviewList = new ArrayList<>();

        for(Review review:reviewRepository.findByUser(user)){
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

    //4. 매장별 리뷰 조회 -> todo: order를 통해 확인한 판매점 정보 이용?
    public List<ReviewResponseDTO> findReviewsByStoreId(UUID storeId) {
        List<ReviewResponseDTO> reviewList = new ArrayList<>();
        List<Order> orderList=/*orderService*/ orderRepository.findAllByStoreStoreId(storeId);

        for(Order order:orderList){
            Review review=reviewRepository.findByOrder(order)
                    .orElseThrow(()->new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));
            reviewList.add(ReviewResponseDTO.builder()
                    .reviewId(review.getReviewId())
                    .rating(review.getRating().toPlainString())
                    .description(review.getDescription())
                    .createdAt(review.getCreatedAt())
                    .createdBy(/*userService*/ userRepository.findByUsername(review.getCreatedBy())
                            .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.")))
                    .updatedAt(review.getUpdatedAt())
                    .updatedBy(review.getUpdatedBy()==null ? null :
                            /*userService*/ userRepository.findByUsername(review.getUpdatedBy())
                            .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.")))
                    .build());
        }
        return reviewList;
    }

    //수정 - 작성자 확인
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER')")
    public void updateReview(UUID reviewId, ReviewUpdateDTO dto,String username) throws AccessDeniedException {
        User user=/*userService*/ userRepository.findByUsername(username)
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

        if(!username.equals(review.getUser().getUsername()))
            throw new AccessDeniedException("해당 권한이 없습니다.");
        review.setIsHide(true);
        review.markAsDeleted(username);
        reviewRepository.save(review);
    }

}
