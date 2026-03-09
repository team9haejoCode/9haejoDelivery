package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.StoreRequestDto;
import com.sparta._9haejodelivery.dto.StoreResponseDto;
import com.sparta._9haejodelivery.dto.StoreUpdateRequestDto;
import com.sparta._9haejodelivery.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "STORE", description = "매장 API")
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // TODO: 권한 (주인만)
    @Operation(summary = "매장 등록", description = "새로운 매장을 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StoreResponseDto> createStore(@Valid @RequestBody StoreRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED, "매장 등록 성공", storeService.createStore(requestDto));
    }

    @Operation(
            summary = "매장 목록 조회",
            description = "매장 목록을 조회합니다. category 또는 sigungu 파라미터로 필터링할 수 있습니다. (중복 사용 불가)"
    )
    @GetMapping
    public ApiResponse<Page<StoreResponseDto>> getStores(
            @Parameter(description = "카테고리명 (예: 한식)") @RequestParam(required = false) String category,
            @Parameter(description = "시/군/구명 (예: 서울특별시 강남구)") @RequestParam(required = false) String sigungu,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "페이지 크기 (10, 30, 50)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 방향 (ASC, DESC)") @RequestParam(defaultValue = "DESC") String sortDirection) {
        if (category != null && sigungu != null) {
            throw new IllegalArgumentException("category와 sigungu는 동시에 사용할 수 없습니다.");
        }
        if (category != null) {
            return ApiResponse.success(HttpStatus.OK, "카테고리별 매장 조회 성공",
                    storeService.getStoresByCategory(category, page, size, sortDirection));
        }
        if (sigungu != null) {
            return ApiResponse.success(HttpStatus.OK, "시/군/구별 매장 조회 성공",
                    storeService.getStoresBySigungu(sigungu, page, size, sortDirection));
        }
        return ApiResponse.success(HttpStatus.OK, "매장 조회 성공",
                storeService.getStores(page, size, sortDirection));
    }

    @Operation(summary = "매장 상세 조회", description = "매장 ID로 단일 매장을 조회합니다.")
    @GetMapping("/{storeId}")
    public ApiResponse<StoreResponseDto> getStore(
            @Parameter(description = "매장 ID") @PathVariable UUID storeId) {
        return ApiResponse.success(HttpStatus.OK, "매장 상세 조회 성공", storeService.getStore(storeId));
    }

    @Operation(summary = "매장 수정", description = "매장 정보를 수정합니다. 전달한 필드만 업데이트됩니다.")
    @PatchMapping("/{storeId}")
    public ApiResponse<StoreResponseDto> updateStore(
            @Parameter(description = "매장 ID") @PathVariable UUID storeId,
            @Valid @RequestBody StoreUpdateRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.OK, "매장 수정 성공", storeService.updateStore(storeId, requestDto));
    }

    @Operation(summary = "매장 삭제", description = "매장을 소프트 삭제합니다.")
    @DeleteMapping("/{storeId}")
    public ApiResponse<Void> deleteStore(
            @Parameter(description = "매장 ID") @PathVariable UUID storeId) {
        storeService.deleteStore(storeId);
        return ApiResponse.success(HttpStatus.OK, "매장 삭제 성공");
    }
}