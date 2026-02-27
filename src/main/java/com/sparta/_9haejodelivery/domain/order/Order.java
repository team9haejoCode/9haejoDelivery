package com.sparta._9haejodelivery.domain.order;

import com.sparta._9haejodelivery.domain.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "P_ORDER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  @Column(length = 100, nullable = false)
  private String address;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems = new ArrayList<>();

  public void addOrderItem(OrderItem orderItem) {
    this.orderItems.add(orderItem);
    // OrderItem의 order 필드를 나(this)로 채워줌 (FK 보장)
    orderItem.assignOrder(this);
  }

  // 연관관계의 주인이 아님을 명시 (Payment 테이블의 order 필드에 의해 매핑됨),
  // 주인(fk필드를 가진 entity)이 아니므로, null값인지 알 수 없어
  // 현재 전략과 상관없이 !무조건 EAGER 전략 적용됨 주의!
  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Payment payment;

  // 연관관계 편의 메서드, this.payment.order == null 그대로 두면 실제 데이터 일관성 오염 주의!
  public void assignPayment(Payment payment) {
    this.payment = payment;
    // DB의 P_ORDER에서는 paymentId를 가지지 않지만,
    // 새로운 order 생성 시,
    // 연관관계의 주인인 Payment 객체에도 Order(나 자신)를 세팅해줘야
    // DB의 P_Payment FK(order_id) 컬럼에 값이 정상적으로 들어갑니다.
    if (payment.getOrder() != this) {
      payment.assignOrder(this);
    }
  }

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
}