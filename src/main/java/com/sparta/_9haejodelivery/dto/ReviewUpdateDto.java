package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewUpdateDTO {//todo: 객체 입력 방식에 따라 수정
    private Order order;    //수정예정
    public String rating;
    public String description;
}
