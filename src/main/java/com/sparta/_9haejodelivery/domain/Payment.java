package com.sparta._9haejodelivery.domain;

import com.sparta._9haejodelivery.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

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
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

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
    
    // 파라미터로 username을 다시 받아서 BaseEntity로 넘겨줍니다.
    public void cancelPayment(String username) {
        this.status = PaymentStatus.CANCELED;
        super.markAsDeleted(username);
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
}