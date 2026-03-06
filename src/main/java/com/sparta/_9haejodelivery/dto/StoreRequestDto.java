package com.sparta._9haejodelivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(description = "매장 등록 요청")
public class StoreRequestDto {

    @Schema(description = "매장명", example = "맛있는 한식당")
    @NotBlank
    private String storeName;

    @Schema(description = "카테고리 ID")
    @NotNull
    private UUID categoryId;

    @Schema(description = "지역 법정동코드 ID (bcodeId)", example = "1111010100")
    @NotBlank
    private String bcodeId;

    @Schema(description = "상세 주소", example = "서울특별시 종로구 청운동 3-55")
    @NotBlank
    private String address;

    @Schema(description = "매장 설명 (최대 100자)", example = "맛있는 한식 전문점입니다.")
    @Size(max = 100)
    private String description;

    @Schema(description = "숨김 여부", example = "false")
    private Boolean isHide;

}
