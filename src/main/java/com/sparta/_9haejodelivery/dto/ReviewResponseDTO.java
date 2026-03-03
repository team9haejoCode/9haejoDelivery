package com.sparta._9haejodelivery.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public class ReviewResponseDTO {    //todo: 객체 입력 방식에 따라 수정
    public UUID reviewId;
    //    public Order order;
    public String rating;
    public String description;
    public LocalDateTime createdAt;
//    public User createdBy;
    public LocalDateTime updatedAt;
//    public User updatedBy;
}
