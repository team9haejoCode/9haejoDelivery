package com.sparta._9haejodelivery.domain;

import com.sparta._9haejodelivery.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID id; 

    @Column(name = "order_id", nullable = false)
    private UUID orderId; 

    @Column(nullable = false)
    private int amount; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "pg_id", length = 30)
    private String pgId;


    public void completePayment(String pgId) {
        this.status = PaymentStatus.COMPLETED;
        this.pgId = pgId;
    }
    
    public void cancelPayment(String username) {
        this.status = PaymentStatus.CANCELED;
        super.markAsDeleted(username); //
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
}