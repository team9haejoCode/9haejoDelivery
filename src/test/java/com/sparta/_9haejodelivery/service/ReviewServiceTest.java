package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.*;
import com.sparta._9haejodelivery.dto.ReviewCreateRequestDto;
import com.sparta._9haejodelivery.dto.ReviewResponseDto;
import com.sparta._9haejodelivery.dto.ReviewUpdateDto;
import com.sparta._9haejodelivery.repository.OrderRepository;
import com.sparta._9haejodelivery.repository.ReviewRepository;
import com.sparta._9haejodelivery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {   //todo: 연동 및 다수의 데이터가 있는 환경에서 다시 테스트
    @Mock
    private ReviewRepository reviewRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;

    private ReviewService reviewService;

    User customer,owner,manager;
    Category category;
    Store store;
    Order order;
    Review review;
    Pageable pageable;
    Region region;

    @BeforeEach
    void setUp() {
        userRepository= Mockito.mock(UserRepository.class);
        orderRepository= Mockito.mock(OrderRepository.class);
        reviewRepository= Mockito.mock(ReviewRepository.class);

        reviewService=new ReviewService(reviewRepository,userRepository,orderRepository);

        pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        region = Region.builder()
                .bcode("bcode")
                .sigungu("sigungu")
                .bcodeId("bcodeId")
                .build();

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
                .storeName("store")
                .category(category)
                .region(region)
                .owner(owner)
                .address("address")
                .description("description")
                .isHide(false).build();

        order = Order.builder()
                .user(customer)
                .store(store)
                .address("address")
                .status(OrderStatus.DELIVERY_COMPLETED).build();

        review = Review.builder()
                .user(customer)
                .order(order)
                .rating(BigDecimal.valueOf(5))
                .description("good")
                .isHide(false).build();

        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(review, "createdBy", customer.getUsername());
        ReflectionTestUtils.setField(store, "storeId", UUID.nameUUIDFromBytes("store".getBytes()));
        ReflectionTestUtils.setField(order, "orderId", UUID.nameUUIDFromBytes("order".getBytes()));
        ReflectionTestUtils.setField(review, "reviewId", UUID.nameUUIDFromBytes("review".getBytes()));
    }


    @Test
    @DisplayName("리뷰 작성 테스트 - 정상처리")
    void createReview() {
        //given
        given(userRepository.findById(customer.getUsername())).willReturn(Optional.of(customer));
        given(orderRepository.findByOrderId(order.getOrderId())).willReturn(Optional.of(order));

        ReviewCreateRequestDto dto = ReviewCreateRequestDto.builder()
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
        given(userRepository.findById(anyString())).willReturn(Optional.empty());

        ReviewCreateRequestDto dto = ReviewCreateRequestDto.builder()
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
        given(userRepository.findById(customer.getUsername())).willReturn(Optional.of(customer));
        given(orderRepository.findByOrderId(order.getOrderId())).willReturn(Optional.empty());

        ReviewCreateRequestDto dto = ReviewCreateRequestDto.builder()
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
        given(userRepository.findById(customer.getUsername())).willReturn(Optional.of(customer));
        given(orderRepository.findByOrderId(order.getOrderId())).willReturn(Optional.of(order));
        given(reviewRepository.findByOrder(order)).willReturn(Optional.of(review));

        ReviewCreateRequestDto dto = ReviewCreateRequestDto.builder()
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
        List<Review> reviews = List.of(review);
        Page<Review> page = new PageImpl<>(reviews, pageable, 1);
        given(reviewRepository.findAll(any(Pageable.class))).willReturn(page);

        //when & then
        Page<ReviewResponseDto> result = reviewService.findAllReviews(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("리뷰 조회 테스트-상세 조회")
    void findReviewById() {
        //given
        given(reviewRepository.findById(review.getReviewId())).willReturn(Optional.of(review));

        //when & then
        ReviewResponseDto result = reviewService.findReviewById(review.getReviewId());
        assertThat(result.getReviewId()).isEqualTo(review.getReviewId());
        assertThat(result.getRating()).isEqualTo(review.getRating().toPlainString());
        assertThat(result.getDescription()).isEqualTo(review.getDescription());
    }

    @Test
    @DisplayName("리뷰 조회 테스트-작성자 필터링")
    void findReviewByUser() {
        //given
        List<Review> reviews = List.of(review);
        Slice<Review> slice = new SliceImpl<>(reviews, pageable, false);

        given(userRepository.findById(customer.getUsername())).willReturn(Optional.of(customer));
        given(reviewRepository.findByUser(customer,pageable)).willReturn(slice);

        //when & then
        Slice<ReviewResponseDto> result = reviewService.findMyReviews(customer.getUsername(),pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("리뷰 조회 테스트-매장별 필터링")
    void findReviewByStore() {
        //given
        List<Review> reviews = List.of(review);
        Slice<Review> slice = new SliceImpl<>(reviews, pageable, false);

        given(reviewRepository.findByStoreId(any(UUID.class),eq(pageable))).willReturn(slice);

        //when & then
        Slice<ReviewResponseDto> result = reviewService.findReviewsByStoreId(store.getStoreId(),pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("리뷰 수정 테스트-정상처리")
    void updateReview() throws AccessDeniedException {
        //given
        given(reviewRepository.findById(review.getReviewId())).willReturn(Optional.of(review));
        given(userRepository.findById(customer.getUsername())).willReturn(Optional.of(customer));
        ReviewUpdateDto dto = ReviewUpdateDto.builder()
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
        given(userRepository.findById(customer.getUsername())).willReturn(Optional.empty());
        ReviewUpdateDto dto = ReviewUpdateDto.builder()
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
        given(userRepository.findById(customer.getUsername())).willReturn(Optional.of(customer));
        given(reviewRepository.findById(review.getReviewId())).willReturn(Optional.empty());
        ReviewUpdateDto dto = ReviewUpdateDto.builder()
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
        given(userRepository.findById(customer.getUsername())).willReturn(Optional.of(customer));

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