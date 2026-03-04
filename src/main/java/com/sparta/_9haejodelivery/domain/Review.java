package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="reviews")
public class Review extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id", nullable = false, updatable = false)
    private UUID reviewId;

//    @Column(name="user_id",nullable = false, updatable = false)
//    @ManyToOne(cascade = CascadeType.ALL)
//    private User user;
//
//    @Column(name = "order_id", nullable = false, updatable = false)
//    @OneToOne(cascade = CascadeType.ALL)
//    private Order order;

    @Range(min = 1, max = 5)
    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "description")
    private String description;

    @Column(name = "is_hide")
    private Boolean isHide=false;
}
