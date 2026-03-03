package com.sparta._9haejodelivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequestDto {
    private String productName;
    private String description;
    private Integer price;
    private String image;
    private Boolean isSoldout;
}
