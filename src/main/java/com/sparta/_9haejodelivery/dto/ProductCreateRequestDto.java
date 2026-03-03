package com.sparta._9haejodelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCreateRequestDto {
    @NotBlank
    private String storeId;
    @NotBlank
    private String productName;
    private String description;
    @NotNull
    @PositiveOrZero
    private Integer price;
    private String image;
    private Boolean isSoldOut;
}
