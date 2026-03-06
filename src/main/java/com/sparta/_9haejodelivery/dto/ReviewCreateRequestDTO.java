package com.sparta._9haejodelivery.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequestDTO {//todo: 객체 입력 방식에 따라 수정
    public UUID orderId;
    public String rating;
    public String description;
}
