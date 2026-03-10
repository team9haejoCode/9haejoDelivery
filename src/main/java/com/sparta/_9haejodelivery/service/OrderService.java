package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.common.BusinessException;
import com.sparta._9haejodelivery.common.ErrorCode;
import com.sparta._9haejodelivery.domain.*;
import com.sparta._9haejodelivery.dto.*;
import com.sparta._9haejodelivery.global.security.UserDetailsImpl;
import com.sparta._9haejodelivery.repository.OrderRepository;
import com.sparta._9haejodelivery.repository.ProductRepository;
import com.sparta._9haejodelivery.repository.StoreRepository;
import com.sparta._9haejodelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final StoreRepository storeRepository;
  private final UserRepository userRepository;

  /**
   * 1. 주문 생성 API
   */
  @Transactional
  public OrderCreateResponseDto createOrder(OrderCreateRequestDto requestDto, String username) {
    // 1. 엔티티 존재 여부 확인
    User user = findUserOrThrow(username);

    Store store = findStoreOrThrow(requestDto.getStoreId());

    if (store.getIsHide())
      throw new BusinessException(ErrorCode.STORE_CLOSED);


    // 2. 주문할 상품들 일괄 조회 (N+1 방지, repository level로 갈 경우 복잡한 로직)
    List<UUID> productIds = requestDto.getItems()
                                      .stream()
                                      .map(OrderCreateRequestDto.OrderItemCreateDto::getProductId)
                                      .toList();

    List<Product> products = productRepository.findAllById(productIds);

    // 조회를 위한 Map 변환 (ID -> Entity)
    Map<UUID, Product> productMap = products.stream().collect(Collectors.toMap(Product::getProductId, p -> p));

    // 3. Order 객체 생성 (Builder 활용)
    Order order = Order.builder()
                       .user(user)
                       .store(store)
                       .address(requestDto.getAddress())
                       .status(OrderStatus.ORDER_ACCEPTED) // 초기 상태 설정
                       .build();

    // 4. 상품 검증 및 OrderItem 추가
    for (OrderCreateRequestDto.OrderItemCreateDto itemDto : requestDto.getItems()) {
      Product product = productMap.get(itemDto.getProductId());

      if (product == null)
        throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);


      // 품절 여부 체크
      if (product.getIsSoldout())
        throw new BusinessException(ErrorCode.PRODUCT_SOLDOUT);


      // OrderItem 생성 (가격을 Product 엔티티에서 가져옴으로써 보안 강화, DB 가격 기준)
      OrderItem orderItem = OrderItem.builder()
                                     .product(product)
                                     .quantity(itemDto.getQuantity())
                                     .unitPrice(product.getPrice())
                                     .build();

      // Order 엔티티의 편의 메서드 호출 (연관관계 설정 + 가격 합산)
      order.addOrderItem(orderItem);
    }

    order.makeOrderSummary();

    // 5. DB 저장 (CascadeType.ALL 설정으로 OrderItem도 함께 저장됨)
    Order savedOrder = orderRepository.save(order);

    // 6. 생성 전용 DTO로 변환하여 반환
    return OrderCreateResponseDto.from(savedOrder);
  }


  /**
   * 2. 주문 단건 상세 조회 (명세서 요구사항)
   */
  public OrderDetailsResponseDto getOrder(UUID orderId, String username) {
    Order order = findOrderWithAllDetailsOrThrow(orderId);

    User user = findUserOrThrow(username);

    // 보안: 관리자가 아니면 본인의 주문만 조회 가능
    validateOrderAuthority(order, user);

    return OrderDetailsResponseDto.from(order);
  }

  /**
   * 3. 주문 내역 검색 (Slice - 무한스크롤(고객/점주용)
   */
  public Slice<OrderSearchResponseDto> getOrdersSlice(
      UserDetailsImpl userDetails, OrderSearchRequestDto requestDto,
      Pageable pageable
  ) {
    OrderSearchCondition condition = OrderSearchCondition.of(requestDto, userDetails);

    return orderRepository.searchOrdersSlice(condition.username(),
                                             requestDto.getStoreId(),
                                             condition.status(),
                                             pageable).map(OrderSearchResponseDto::from); // 깔끔한 메서드 참조
  }

  /**
   * 4. 주문 내역 검색 (Page - 관리자용)
   */
  public Page<OrderSearchResponseDto> getOrdersPage(
      UserDetailsImpl userDetails, OrderSearchRequestDto requestDto,
      Pageable pageable
  ) {
    OrderSearchCondition condition = OrderSearchCondition.of(requestDto, userDetails);

    return orderRepository.searchOrdersPage(condition.username(), requestDto.getStoreId(), condition.status(), pageable)
                          .map(OrderSearchResponseDto::from);
  }

  /**
   * 5. 주문 상태 변경
   */
  @Transactional
  public OrderUpdateResponseDto updateOrderStatus(
      UUID orderId, OrderUpdateStatusRequestDto requestDto,
      UserDetailsImpl userDetails
  ) {
    // 비관적 락
    Order order = orderRepository.findByIdForUpdate(orderId)
                                 .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    // 현재 DB 유저 정보 기준
    User user = findUserOrThrow(userDetails.getUsername());

    // 보안: 현재 DB 기준 유효한 점주 본인/관리자/주문 고객 당사자만 상태 수정 가능
    validateOrderAuthority(order, user);

    // 비즈니스 규칙: 최종 완료되거나 거절된 주문은 상태 변경 불가
    order.changeStatus(requestDto.getStatus(), user.getRole());

    return OrderUpdateResponseDto.from(order);
  }

  /**
   * 6. 주문 주소 변경
   */
  @Transactional
  public OrderUpdateResponseDto updateOrderAddress(
      UUID orderId, OrderUpdateAddressRequestDto requestDto,
      UserDetailsImpl userDetails
  ) {
    Order order = orderRepository.findByIdForUpdate(orderId)
                                 .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    // 현재 DB 유저 정보 기준
    User user = findUserOrThrow(userDetails.getUsername());

    if (user.getRole().equals(UserRole.OWNER))
      throw new BusinessException(ErrorCode.ORDER_ADDRESS_NOT_CHANGEABLE_BY_OWNER);

    // 보안: 현재 DB 기준 유효한 점주 본인/관리자/주문 고객 당사자만 상태 수정 가능
    validateOrderAuthority(order, user);

    // 비즈니스 규칙: 주문 접수 상태의 주문만 주소 변경 가능
    order.reviseAddress(requestDto.getAddress());

    return OrderUpdateResponseDto.from(order);
  }

  /**
   * 6. 주문 삭제(관리자용)
   */
  @Transactional
  public OrderDeleteResponseDto deleteOrder(UUID orderId, String username) {
    Order order = findOrderOrThrow(orderId);
    User user = findUserOrThrow(username);

    // 보안: 관리자, 매니저만 삭제 가능
    if (!(user.getRole().equals(UserRole.MASTER) || user.getRole().equals(UserRole.MANAGER))) {
      throw new BusinessException(ErrorCode.ORDER_DELETE_NOT_ALLOWED);
    }

    order.markAsDeleted(username);

    return OrderDeleteResponseDto.of(order, "주문이 정상적으로 삭제되었습니다.");
  }

  // --- Util Methods ---

  private Order findOrderOrThrow(UUID orderId) {
    return orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
  }

  private Order findOrderWithAllDetailsOrThrow(UUID orderId) {
    return orderRepository.findByIdWithAllDetails(orderId)
                          .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
  }

  private User findUserOrThrow(String username) {
    return userRepository.findById(username).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
  }

  private Store findStoreOrThrow(UUID storeId) {
    return storeRepository.findById(storeId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
  }


  /**
   * 권한 검증: 관리자면 통과, 주문을 관리하는 가게 주인(owner) 혹은 주문 당사자인지
   * status/address 수정 전 확인
   */
  private void validateOrderAuthority(Order order, User requestUser) {
    // 마스터/매니저는 통과
    if (requestUser.getRole().equals(UserRole.MASTER) || requestUser.getRole().equals(UserRole.MANAGER)) {
      return;
    }

    if (requestUser.getRole().equals(UserRole.CUSTOMER)) {
      if (order.getUser().getUsername().equals(requestUser.getUsername())) {
        return; // 본인 주문이면 통과
      } else {
        throw new BusinessException(ErrorCode.ORDER_NOT_YOURS);
      }
    }

    // 3. 가게 주인(OWNER)인 경우 해당 가게 주문인지 확인
    if (requestUser.getRole().equals(UserRole.OWNER)) {
      if (order.getStore().getOwner().equals(requestUser)) {
        return; // 본인 가게 주문이면 통과
      } else {
        throw new BusinessException(ErrorCode.ORDER_NOT_YOUR_STORE);
      }
    }

    throw new BusinessException(ErrorCode.ACCESS_DENIED);
  }

}