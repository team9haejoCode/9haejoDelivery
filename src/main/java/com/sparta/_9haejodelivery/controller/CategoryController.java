package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.CategoryRequestDto;
import com.sparta._9haejodelivery.dto.CategoryResponseDto;
import com.sparta._9haejodelivery.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED, "카테고리 생성 성공", categoryService.createCategory(requestDto));
    }

    @GetMapping
    public ApiResponse<List<CategoryResponseDto>> getCategories() {
        return ApiResponse.success(HttpStatus.OK, "카테고리 조회 성공", categoryService.getCategories());
    }

    @PatchMapping("/{categoryId}")
    public ApiResponse<CategoryResponseDto> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.OK, "카테고리 수정 성공", categoryService.updateCategory(categoryId, requestDto));
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(
            @PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.success(HttpStatus.OK, "카테고리 삭제 성공");
    }
}