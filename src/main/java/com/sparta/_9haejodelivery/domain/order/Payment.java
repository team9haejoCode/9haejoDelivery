package com.sparta._9haejodelivery.domain.order;

import com.sparta._9haejodelivery.domain.order.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "P_PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID paymentId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  private Integer amount;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Column(length = 30)
  private String pgId;

  // 클래스 레벨이 아닌, 필요한 필드만 포함된 생성자에 빌더 선언, payment의 경우 확장성 고려
  @Builder
  public Payment(
      Integer amount,
      PaymentStatus status
  ) {
    this.amount = amount;
    this.status = status != null ? status : PaymentStatus.PAYMENT_PENDING;
  }

  // Setter 대신 의도가 담긴 명칭 + default로 entity 패키지 외부 접근 제한
  void assignOrder(Order order) {
    this.order = order;
  }
}
