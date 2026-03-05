package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.*;
import com.sparta._9haejodelivery.dto.ReviewCreateRequestDTO;
import com.sparta._9haejodelivery.dto.ReviewResponseDTO;
import com.sparta._9haejodelivery.dto.ReviewUpdateDTO;
import com.sparta._9haejodelivery.repository.ReviewRepository;
import com.sparta._9haejodelivery.repository.temp_OrderRepository;
import com.sparta._9haejodelivery.repository.temp_UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {   //todo: 연동 및 다수의 데이터가 있는 환경에서 다시 테스트
    @Mock
    private ReviewRepository reviewRepository;
    private temp_OrderRepository orderRepository;
    private temp_UserRepository userRepository;

    private ReviewService reviewService;

    User customer,owner,manager;
    Category category;
    Store store;
    Order order;
    Review review;

    @BeforeEach
    void setUp() {
        userRepository= Mockito.mock(temp_UserRepository.class);
        orderRepository= Mockito.mock(temp_OrderRepository.class);
        reviewRepository= Mockito.mock(ReviewRepository.class);

        reviewService=new ReviewService(reviewRepository,userRepository,orderRepository);

        customer = User.builder()
                .username("customer")
                .nickname("tester1")
                .password("1234")
                .address("address")
                .role(UserRole.CUSTOMER).build();

        owner = User.builder()
                .username("owner")
                .nickname("tester2")
                .password("1234")
                .address("address")
                .role(UserRole.OWNER).build();

        manager = User.builder()
                .username("manager")
                .nickname("tester3")
                .password("1234")
                .address("address")
                .role(UserRole.MANAGER).build();

        category = Category.builder()
                .categoryName("category")
                .build();

        store = Store.builder()
                .storeId(UUID.nameUUIDFromBytes("store".getBytes()))
                .storeName("store")
                .category(category)
                .regionId(UUID.nameUUIDFromBytes("region".getBytes()))
                .owner(owner)
                .address("address")
                .description("description")
                .isHide(false).build();

        order = Order.builder()
                .orderId(UUID.nameUUIDFromBytes("order".getBytes()))
                .user(customer)
                .store(store)
                .address("address")
                .status(temp_OrderStatus.DELIVERY_COMPLETED).build();

        review = Review.builder()
                .reviewId(UUID.nameUUIDFromBytes("review".getBytes()))
                .user(customer)
                .order(order)
                .rating(BigDecimal.valueOf(5))
                .description("good")
                .isHide(false).build();

        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(review, "createdBy", customer.getUsername());
    }


    @Test
    @DisplayName("리뷰 작성 테스트 - 정상처리")
    void createReview() throws AccessDeniedException {
        //given
        given(userRepository.findByUsername(customer.getUsername())).willReturn(Optional.of(customer));
        given(orderRepository.findById(order.getOrderId())).willReturn(Optional.of(order));

        ReviewCreateRequestDTO dto = ReviewCreateRequestDTO.builder()
                .orderId(order.getOrderId())
                .rating("5")
                .description("good")
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        //when
        reviewService.createReview(dto, customer.getUsername());

        //then
        ArgumentCaptor<Review> reviewArgumentCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgumentCaptor.capture());
        Review review = reviewArgumentCaptor.getValue();
        assertThat(review).isNotNull();
        assertThat(review.getOrder().getOrderId()).isEqualTo(order.getOrderId());
        assertThat(review.getRating()).isEqualTo(dto.getRating());
        assertThat(review.getDescription()).isEqualTo(dto.getDescription());
        assertThat(review.getUser().getUsername()).isEqualTo(customer.getUsername());
    }

    @Test
    @DisplayName("리뷰 작성 테스트 - 사용자 조회 실패")
    void createReview_NotFoundUser() {
        //given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        ReviewCreateRequestDTO dto = ReviewCreateRequestDTO.builder()
                .orderId(order.getOrderId())
                .rating("5")
                .description("good")
                .build();

        //when & then
        assertThatThrownBy(() -> reviewService.createReview(dto, customer.getUsername()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("리뷰 작성 테스트 - 주문 조회 실패")
    void createReview_NotFoundOrder() {
        //given
        given(userRepository.findByUsername(customer.getUsername())).willReturn(Optional.of(customer));
        given(orderRepository.findById(order.getOrderId())).willReturn(Optional.empty());

        ReviewCreateRequestDTO dto = ReviewCreateRequestDTO.builder()
                .orderId(order.getOrderId())
                .rating("5")
                .description("good")
                .build();

        //when & then
        assertThatThrownBy(() -> reviewService.createReview(dto, customer.getUsername()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 주문 내역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("리뷰 작성 테스트 - 이미 해당 주문에 대한 리뷰를 작성했던 경우")
    void createReview_ExistReview() {
        //given
        given(userRepository.findByUsername(customer.getUsername())).willReturn(Optional.of(customer));
        given(orderRepository.findById(order.getOrderId())).willReturn(Optional.of(order));
        given(reviewRepository.findByOrder(order)).willReturn(Optional.of(review));

        ReviewCreateRequestDTO dto = ReviewCreateRequestDTO.builder()
                .orderId(order.getOrderId())
                .rating("5")
                .description("good")
                .build();

        //when & then
        assertThatThrownBy(() -> reviewService.createReview(dto, customer.getUsername()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 해당 주문에 대해 리뷰를 작성했습니다.");
    }

    @Test
    @DisplayName("리뷰 조회 테스트-전체 조회")
    void findAllReviews() {
        //given
        given(reviewRepository.findAll()).willReturn(List.of(review));

        //when & then
        List<ReviewResponseDTO> result = reviewService.findAllReviews();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("리뷰 조회 테스트-상세 조회")
    void findReviewById() {
        //given
        given(reviewRepository.findById(review.getReviewId())).willReturn(Optional.of(review));

        //when & then
        ReviewResponseDTO result = reviewService.findReviewById(review.getReviewId());
        assertThat(result.getReviewId()).isEqualTo(review.getReviewId());
        assertThat(result.getRating()).isEqualTo(review.getRating().toPlainString());
        assertThat(result.getDescription()).isEqualTo(review.getDescription());
    }

    @Test
    @DisplayName("리뷰 조회 테스트-작성자 필터링")
    void findReviewByUser() {
        //given
        given(userRepository.findByUsername(customer.getUsername())).willReturn(Optional.of(customer));
        given(reviewRepository.findByUser(customer)).willReturn(List.of(review));
        given(reviewRepository.findByUser(customer)).willReturn(List.of(review));

        //when & then
        List<ReviewResponseDTO> result = reviewService.findMyReviews(customer.getUsername());
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("리뷰 조회 테스트-매장별 필터링")
    void findReviewByStore() {
        //given
        given(userRepository.findByUsername(customer.getUsername())).willReturn(Optional.of(customer));
        given(orderRepository.findAllByStoreStoreId(store.getStoreId())).willReturn(List.of(order));
        given(reviewRepository.findByOrder(order)).willReturn(Optional.of(review));

        //when & then
        List<ReviewResponseDTO> result = reviewService.findReviewsByStoreId(store.getStoreId());
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("리뷰 수정 테스트-정상처리")
    void updateReview() throws AccessDeniedException {
        //given
        given(reviewRepository.findById(review.getReviewId())).willReturn(Optional.of(review));
        given(userRepository.findByUsername(customer.getUsername())).willReturn(Optional.of(customer));
        ReviewUpdateDTO dto = ReviewUpdateDTO.builder()
                .rating("1")
                .description("bad")
                .build();

        //when
        reviewService.updateReview(review.getReviewId(), dto, customer.getUsername());

        //then
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        Review review = captor.getValue();
        assertThat(review.getRating()).isEqualTo(dto.getRating());
        assertThat(review.getDescription()).isEqualTo(dto.getDescription());
    }

    @Test
    @DisplayName("리뷰 수정 테스트-사용자 탐색 실패")
    void updateReview_NotFoundUser() {
        //given
        given(userRepository.findByUsername(customer.getUsername())).willReturn(Optional.empty());
        ReviewUpdateDTO dto = ReviewUpdateDTO.builder()
                .rating("1")
                .description("bad")
                .build();

        //when & then
        assertThatThrownBy(() -> reviewService.updateReview(review.getReviewId(), dto, customer.getUsername()))
                .hasMessage("해당 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("리뷰 수정 테스트-리뷰 탐색 실패")
    void updateReview_NotFoundReview() {
        //given
        given(userRepository.findByUsername(customer.getUsername())).willReturn(Optional.of(customer));
        given(reviewRepository.findById(review.getReviewId())).willReturn(Optional.empty());
        ReviewUpdateDTO dto = ReviewUpdateDTO.builder()
                .rating("1")
                .description("bad")
                .build();

        //when & then
        assertThatThrownBy(() -> reviewService.updateReview(review.getReviewId(), dto, customer.getUsername()))
                .hasMessage("해당 리뷰를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("리뷰 정상 삭제-정상처리")
    void deleteReview() throws AccessDeniedException {
        //given
        given(reviewRepository.findById(review.getReviewId())).willReturn(Optional.of(review));

        //when
        reviewService.deleteReview(review.getReviewId(),customer.getUsername());

        //then
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        assertThat(captor.getValue().getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("리뷰 정상 삭제-리뷰 탐색 실패")
    void deleteReview_NotFoundReview() {
        //given
        given(reviewRepository.findById(review.getReviewId())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> reviewService.deleteReview(review.getReviewId(),customer.getUsername()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 리뷰를 찾을 수 없습니다.");
    }
}