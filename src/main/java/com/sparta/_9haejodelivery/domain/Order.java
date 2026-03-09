package com.sparta._9haejodelivery.domain;

import com.sparta._9haejodelivery.domain.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "status", columnDefinition = "order_status")
  private OrderStatus status;

  @Column(nullable = false, length = 100)
  private String address;

  @Column(nullable = false)
  private Integer totalPrice = 0;

  @Column(name = "order_summary", nullable = false, length = 100)
  private String orderSummary;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItemEntities = new ArrayList<>();

  @Builder
  public Order(User user, Store store, String address, OrderStatus status) {
    this.user = user;
    this.store = store;
    this.address = address;
    this.status = status != null ? status : OrderStatus.ORDER_ACCEPTED;
  }

  /**
   * [상태 제어 헬퍼 메서드]
   */
  public boolean isStateEditable() {
    // 최종 완료되거나 거절된 주문, 고객이 취소한 주문은 수정 불가
    return !(this.status == OrderStatus.DELIVERY_COMPLETED || this.status == OrderStatus.ORDER_REJECTED ||
             this.status == OrderStatus.ORDER_CANCELED);
  }

  public boolean isAddressEditable() {
    // 주문 수락된 경우 외에 주소 수정 불가
    return status == OrderStatus.ORDER_ACCEPTED;
  }

  public boolean isCancelable() {
    // 추가 규칙: 주문 접수일 때만 고객이 취소 가능
    return status == OrderStatus.ORDER_ACCEPTED;
  }

  /**
   * 비즈니스 로직(편의 메서드)
   */

  public void addOrderItem(OrderItem orderItem) {
    this.orderItemEntities.add(orderItem);
    orderItem.assignOrder(this);

    this.totalPrice = (this.totalPrice == null ? 0 : this.totalPrice) + orderItem.getSubTotal();
  }

  // 주문 생성 시 요약 정보 작성
  public void makeOrderSummary() {
    List<OrderItem> items = this.getOrderItemEntities();
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("주문에는 최소 1개 이상의 상품이 포함되어야 합니다.");
    }

    // 첫 번째 상품명 추출
    String firstProductName = items.getFirst().getProduct().getProductName();
    int extraCount = items.size() - 1;

    this.orderSummary = extraCount > 0 ? String.format("%s 외 %d개", firstProductName, extraCount) : firstProductName;
  }

  public void changeStatus(OrderStatus newStatus, UserRole requesterRole) {
    // 1. 공통 규칙: 이미 최종 상태(완료/거절/취소)라면 어떤 변경도 불가능
    if (!isStateEditable())
      throw new IllegalStateException("배달 완료/점주 거절/고객 취소 상태는 변경할 수 없습니다.");


    // 2. 고객(CUSTOMER) 전용 규칙
    if (requesterRole.equals(UserRole.CUSTOMER)) {
      if (newStatus != OrderStatus.ORDER_CANCELED)
        throw new AccessDeniedException("고객은 주문 취소 요청만 가능합니다.");


      if (!isCancelable())
        throw new IllegalStateException("조리가 시작된 주문은 취소할 수 없습니다.");

      // 5분 후 취소 제한
      checkCancelTimeout();
    }

    // 3. 점주(OWNER) 전용 규칙
    if (requesterRole.equals(UserRole.OWNER) || requesterRole.equals(UserRole.MANAGER)) {
      if (newStatus == OrderStatus.ORDER_CANCELED)
        throw new AccessDeniedException("점주/매니저는 주문 취소 요청을 할 수 없습니다.");
    }

    // 4. 상태 흐름 규칙: 이전 단계로 되돌리기 방지
    // (예: 배달 중 -> 접수 완료로 변경 불가)
    if (this.status.isAfter(newStatus))
      throw new IllegalStateException("이전 단계의 상태로 되돌릴 수 없습니다.");


    // 5. 최종 상태 업데이트
    this.status = newStatus;
  }

  public void reviseAddress(String newAddress) {
    if (newAddress == null || newAddress.isBlank()) {
      throw new IllegalArgumentException("주소는 비어있을 수 없습니다.");
    }

    if (!isAddressEditable()) {
      throw new IllegalStateException("주문 수락 상태 외의 경우, 주소를 변경할 수 없습니다.");
    }

    this.address = newAddress;
  }


  // 주문 상품 가격 재계산
  public void calculateTotalPrice() {
    this.totalPrice = this.orderItemEntities.stream().mapToInt(OrderItem::getSubTotal).sum();
  }

  /**
   * 주문 생성 후 5분이 지났는지 확인하는 내부 메서드
   */
  private void checkCancelTimeout() {
    LocalDateTime now = LocalDateTime.now();
    if (getCreatedAt().plusMinutes(5).isBefore(now)) {
      throw new IllegalStateException("주문 생성 후 5분이 경과하여 취소할 수 없습니다.");
    }
  }

}