package com.sparta._9haejodelivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryRequestDto {

    @NotBlank
    private String categoryName;
}