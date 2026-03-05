package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

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
  @Column(nullable = false, length = 20)
  private OrderStatus status;

  @Column(nullable = false, length = 100)
  private String address;

  @Column(nullable = false)
  private Integer totalPrice = 0;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItemEntities = new ArrayList<>();

  @Builder
  public Order(
      User user,
      Store store,
      String address,
      OrderStatus status
  ) {
    this.user = user;
    this.store = store;
    this.address = address;
    this.status = status != null ? status : OrderStatus.ORDER_ACCEPTED;
  }

  /**
   * [상태 제어 헬퍼 메서드]
   */
  public boolean isStateEditable() {
    // 최종 완료되거나 거절된 주문은 수정 불가
    return !(this.status == OrderStatus.DELIVERY_COMPLETED || this.status == OrderStatus.ORDER_REJECTED);
  }

  public boolean isAddressEditable() {
    // 주문 수락된 경우 외에 주소 수정 불가
    return status == OrderStatus.ORDER_ACCEPTED;
  }

  /**
   * 비즈니스 로직(편의 메서드)
   */

  public void addOrderItem(OrderItem orderItem) {
    this.orderItemEntities.add(orderItem);
    orderItem.assignOrder(this);

    this.totalPrice += orderItem.getSubTotal();
  }

  public void changeStatus(OrderStatus newStatus) {
    if (!isStateEditable()) {
      throw new IllegalStateException("최종 완료되거나 거절된 주문은 상태를 변경할 수 없습니다.");
    }

    // 예: 배달 중(DELIVERING)인데 다시 수락(ACCEPTED)으로 바꿀 수 없도록 방어
    // enum 내부에서 관리하거나 여기서 체크
    if (this.status.isAfter(newStatus)) {
      throw new IllegalStateException("이전 단계의 상태로 되돌릴 수 없습니다.");
    }

    this.status = newStatus;
  }

  public void reviseAddress(String newAddress) {
    if (newAddress == null || newAddress.isBlank()) {
      throw new IllegalArgumentException("주소는 비어있을 수 없습니다.");
    }

    if (!isAddressEditable()) {
      throw new IllegalStateException("조리가 시작되었거나 배달 중인 주문, 이미 완료된 주문은 주소를 변경할 수 없습니다.");
    }

    this.address = newAddress;
  }


  // 주문 상품 가격 재계산
  public void calculateTotalPrice() {
    this.totalPrice = this.orderItemEntities.stream()
                                            .mapToInt(OrderItem::getSubTotal)
                                            .sum();
  }


}