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
@SQLRestriction("deleted_at IS NULL") // 팀의 변경 사항에 맞춘 Soft Delete 필터링
public class PaymentEntity extends BaseEntity {

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
    
    public void cancelPayment(String username) {
        this.status = PaymentStatus.CANCELED;
        super.markAsDeleted(username);
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
}