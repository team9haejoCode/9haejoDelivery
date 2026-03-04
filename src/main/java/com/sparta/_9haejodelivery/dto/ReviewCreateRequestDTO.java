package com.sparta._9haejodelivery.dto;


import lombok.Getter;

import java.util.UUID;

@Getter
public class ReviewCreateRequestDTO {//todo: 객체 입력 방식에 따라 수정
    private UUID orderID;
    public String rating;
    public String description;
}
