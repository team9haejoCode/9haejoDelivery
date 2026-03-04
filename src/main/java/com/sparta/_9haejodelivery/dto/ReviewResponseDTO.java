package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {    //todo: 객체 입력 방식에 따라 수정
    public UUID reviewId;
    public Order order; //todo: 주문 수정 예정
    public String rating;
    public String description;
    public LocalDateTime createdAt;
    public User createdBy;
    public LocalDateTime updatedAt;
    public User updatedBy;
}
