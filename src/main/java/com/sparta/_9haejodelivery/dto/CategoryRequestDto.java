package com.sparta._9haejodelivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "카테고리 요청")
public class CategoryRequestDto {

    @Schema(description = "카테고리명", example = "한식")
    @NotBlank
    private String categoryName;
}