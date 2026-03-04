package com.sparta._9haejodelivery.domain;

import com.sparta._9haejodelivery.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID; // UUID import 필수

@Entity
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 본인 ID는 기존 DB대로 Long 유지

    // Long -> UUID 로 타입 변경
    @Column(name = "order_id", nullable = false)
    private UUID orderId; 

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "pg_id")
    private String pgId;

    public void completePayment(String pgId) {
        this.status = PaymentStatus.COMPLETED;
        this.pgId = pgId;
    }
    
    public void cancelPayment(String username) {
        this.status = PaymentStatus.CANCELED;
        super.markAsDeleted(username);
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
}