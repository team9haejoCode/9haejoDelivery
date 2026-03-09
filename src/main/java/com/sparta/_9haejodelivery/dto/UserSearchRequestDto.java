package com.sparta._9haejodelivery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequestDto {
    private int page = 1;      // 기본값 1페이지
    private int size = 10;     // 기본값 10개
    private String sortBy = "createdAt"; // 요구사항: 기본 생성일순

    public static int validateSize(int inputSize) {
        if (inputSize == 10 || inputSize == 30 || inputSize == 50) {
            return inputSize;
        }
        return 10; // 기본값
    }
}