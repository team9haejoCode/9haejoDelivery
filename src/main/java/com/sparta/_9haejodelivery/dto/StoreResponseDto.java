package com.sparta._9haejodelivery.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class StoreResponseDto {

    private UUID storeId;
    private String storeName;
    private String categoryName;
    private UUID regionId;
    private String address;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}