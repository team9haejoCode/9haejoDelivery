package com.sparta._9haejodelivery.domain.order;

import com.sparta._9haejodelivery.domain.order.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "P_PAYMENT_HISTORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID paymentHistoryId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentStatus previousStatus;

  @Enumerated(EnumType.STRING)
  private PaymentStatus currentStatus;

  @Column(length = 100)
  private String significant;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;
}
