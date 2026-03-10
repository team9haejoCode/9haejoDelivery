package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.CategoryRequestDto;
import com.sparta._9haejodelivery.dto.CategoryResponseDto;
import com.sparta._9haejodelivery.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "CATEGORY", description = "카테고리 API")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "카테고리 생성 (관리자)", description = "새로운 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "카테고리 생성 성공", categoryService.createCategory(requestDto)));
    }

    @Operation(summary = "카테고리 목록 조회", description = "전체 카테고리 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "카테고리 조회 성공", categoryService.getCategories()));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "카테고리 수정 (관리자)", description = "카테고리 이름을 수정합니다.")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(
            @Parameter(description = "카테고리 ID") @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "카테고리 수정 성공", categoryService.updateCategory(categoryId, requestDto)));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "카테고리 삭제 (관리자)", description = "카테고리를 소프트 삭제합니다.")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "카테고리 ID") @PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "카테고리 삭제 성공"));
    }
}