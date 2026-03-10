package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {
    private String productId;
    private String storeId;
    private String productName;
    private String description;
    private Integer price;
    private String image;
    private Boolean isSoldout;

    public static ProductResponseDto from(Product entity) {
        return ProductResponseDto.builder()
                .productId(entity.getProductId().toString())
                .storeId(entity.getStore().toString())
                .productName(entity.getProductName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .image(entity.getImage())
                .isSoldout(entity.getIsSoldout())
                .build();
    }
}
