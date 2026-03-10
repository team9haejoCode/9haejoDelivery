package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(description = "카테고리 응답")
public class CategoryResponseDto {

    @Schema(description = "카테고리 ID")
    private final UUID categoryId;

    @Schema(description = "카테고리명", example = "한식")
    private final String categoryName;

    public CategoryResponseDto(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
    }
}