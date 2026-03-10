package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="P_REVIEWS")
public class Review extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id", nullable = false, updatable = false)
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne()
    private Order order;

    @Range(min = 1, max = 5)
    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "description")
    private String description;

    @Column(name = "is_hide")
    private Boolean isHide=false;

    @Builder
    public Review(User user, Order order, BigDecimal rating, String description, Boolean isHide) {
        this.user = user;
        this.order = order;
        this.rating = rating;
        this.description = description;
        this.isHide = isHide;
    }
}
