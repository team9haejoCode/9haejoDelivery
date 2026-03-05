package com.sparta._9haejodelivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(description = "매장 수정 요청 (전달한 필드만 업데이트)")
public class StoreUpdateRequestDto {

    @Schema(description = "매장명", example = "맛있는 한식당")
    private String storeName;

    @Schema(description = "카테고리 ID")
    private UUID categoryId;

    @Schema(description = "지역 ID")
    private UUID regionId;

    @Schema(description = "상세 주소", example = "서울시 강남구 역삼동 123-45")
    private String address;

    @Schema(description = "매장 설명 (최대 100자)", example = "맛있는 한식 전문점입니다.")
    @Size(max = 100)
    private String description;

    @Schema(description = "숨김 여부", example = "false")
    private Boolean isHide;

}
