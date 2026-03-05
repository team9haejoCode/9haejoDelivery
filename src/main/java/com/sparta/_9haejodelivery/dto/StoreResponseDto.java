package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Region;
import com.sparta._9haejodelivery.domain.Store;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StoreResponseDto {

    private final UUID storeId;
    private final String storeName;
    private final String categoryName;
    private final Region region;
    private final String address;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public StoreResponseDto(Store store) {
        this.storeId = store.getStoreId();
        this.storeName = store.getStoreName();
        this.categoryName = store.getCategory().getCategoryName();
        this.region = store.getRegion();
        this.address = store.getAddress();
        this.description = store.getDescription();
        this.createdAt = store.getCreatedAt();
        this.updatedAt = store.getUpdatedAt();
    }
}