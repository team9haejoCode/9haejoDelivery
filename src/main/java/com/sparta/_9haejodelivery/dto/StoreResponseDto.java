package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Region;
import com.sparta._9haejodelivery.domain.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Schema(description = "매장 응답")
public class StoreResponseDto {

    @Schema(description = "매장 ID")
    private final UUID storeId;

    @Schema(description = "매장명", example = "맛있는 한식당")
    private final String storeName;

    @Schema(description = "카테고리명", example = "한식")
    private final String categoryName;

    @Schema(description = "지역 정보")
    private final Region region;

    @Schema(description = "상세 주소", example = "서울시 강남구 역삼동 123-45")
    private final String address;

    @Schema(description = "매장 설명")
    private final String description;

    @Schema(description = "생성 일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 일시")
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