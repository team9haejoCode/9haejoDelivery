package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.Review;
import com.sparta._9haejodelivery.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ReviewResponseDto {    //todo: 객체 입력 방식에 따라 수정
    public UUID reviewId;
    public Order order; //todo: 주문 수정 예정
    public String rating;
    public String description;
    public LocalDateTime createdAt;
    public User createdBy;
    public LocalDateTime updatedAt;
    public User updatedBy;

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getReviewId();
        this.rating = review.getRating().toPlainString();
        this.description = review.getDescription();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }
}
