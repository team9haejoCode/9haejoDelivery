package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.Category;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryResponseDto {

    private final UUID categoryId;
    private final String categoryName;

    public CategoryResponseDto(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
    }
}