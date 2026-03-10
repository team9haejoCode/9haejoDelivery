package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.ProductCreateRequestDto;
import com.sparta._9haejodelivery.dto.ProductResponseDto;
import com.sparta._9haejodelivery.dto.ProductUpdateRequestDto;
import com.sparta._9haejodelivery.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 생성 API, 권한: OWNER
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDto> createProduct(
            @RequestPart(value = "requestDto") @Valid ProductCreateRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        ProductResponseDto response = productService.createProduct(requestDto, image);
        return ApiResponse.success(HttpStatus.OK, "상품 생성 성공", response);
    }

    // 상품 수정 API, 권한: OWNER
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/products/{productId}")
    public ApiResponse<ProductResponseDto> updateProduct(
            @PathVariable UUID productId,
            @RequestBody ProductUpdateRequestDto requestDto) {
        ProductResponseDto response = productService.updateProduct(productId, requestDto);
        return ApiResponse.success(HttpStatus.OK, "상품 수정 성공", response);
    }

    // 상품 전체 조회 API, 권한: MANAGER, MASTER
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @GetMapping("/products")
    public ApiResponse<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> response = productService.getAllProducts();
        return ApiResponse.success(HttpStatus.OK, "상품 전체 조회 성공", response);
    }

    // 매장 별 상품 조회 API
    @GetMapping("/stores/{storeId}/products")
    public ApiResponse<List<ProductResponseDto>> getProductsByStore(@PathVariable UUID storeId) {
        List<ProductResponseDto> response = productService.getProductsByStore(storeId);
        return ApiResponse.success(HttpStatus.OK, "매장 별 상품 조회 성공", response);
    }

    // 상품 상세 조회 API
    @GetMapping("/products/{productId}")
    public ApiResponse<ProductResponseDto> getProductDetail(@PathVariable UUID productId) {
        ProductResponseDto response = productService.getProductDetail(productId);
        return ApiResponse.success(HttpStatus.OK, "상품 상세 조회 성공", response);
    }

    // 상품 삭제 API, 권한: OWNER, MANAGER, MASTER
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @DeleteMapping("/products/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable UUID productId) {
        // TODO: 향후 실제 로그인 한 유저 ID를 넘기도록 수정
        productService.deleteProduct(productId, "system_user");
        return ApiResponse.success(HttpStatus.OK, "상품 삭제 성공");
    }
}