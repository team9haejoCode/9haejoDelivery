package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.StoreRequestDto;
import com.sparta._9haejodelivery.dto.StoreResponseDto;
import com.sparta._9haejodelivery.dto.StoreUpdateRequestDto;
import com.sparta._9haejodelivery.service.StoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // 매장 등록
    // TODO: 권한 (주인만)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StoreResponseDto> createStore(@Valid @RequestBody StoreRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.CREATED, "매장 등록 성공", storeService.createStore(requestDto));
    }

    // 매장 조회 + 카테고리별 조회 + 지역별 조회
    @GetMapping
    public ApiResponse<Page<StoreResponseDto>> getStores(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UUID regionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        if (category != null) {
            return ApiResponse.success(HttpStatus.OK, "카테고리별 매장 조회 성공",
                    storeService.getStoresByCategory(category, page, size, sortDirection));
        }
        if (regionId != null) {
            return ApiResponse.success(HttpStatus.OK, "지역별 매장 조회 성공",
                    storeService.getStoresByRegion(regionId, page, size, sortDirection));
        }
        return ApiResponse.success(HttpStatus.OK, "매장 조회 성공",
                storeService.getStores(page, size, sortDirection));
    }

    // 매장 상세 조회
    @GetMapping("/{storeId}")
    public ApiResponse<StoreResponseDto> getStore(@PathVariable UUID storeId) {
        return ApiResponse.success(HttpStatus.OK, "매장 상세 조회 성공", storeService.getStore(storeId));
    }

    // 매장 수정
    @PatchMapping("/{storeId}")
    public ApiResponse<StoreResponseDto> updateStore(
            @PathVariable UUID storeId,
            @Valid @RequestBody StoreUpdateRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.OK, "매장 수정 성공", storeService.updateStore(storeId, requestDto));
    }

    // 매장 삭제
    @DeleteMapping("/{storeId}")
    public ApiResponse<Void> deleteStore(@PathVariable UUID storeId) {
        storeService.deleteStore(storeId);
        return ApiResponse.success(HttpStatus.OK, "매장 삭제 성공");
    }
}