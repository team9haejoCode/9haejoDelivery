package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.ProductEntity;
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
    private Boolean isSoldOut;

    public static ProductResponseDto from(ProductEntity entity) {
        return ProductResponseDto.builder()
                .productId(entity.getProductId().toString())
                .storeId(entity.getStoreId().toString())
                .productName(entity.getProductName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .image(entity.getImage())
                .isSoldOut(entity.getIsSoldout())
                .build();
    }
}
