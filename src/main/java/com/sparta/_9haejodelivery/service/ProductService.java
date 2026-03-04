package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.ProductEntity;
import com.sparta._9haejodelivery.domain.StoreEntity;
import com.sparta._9haejodelivery.dto.ProductCreateRequestDto;
import com.sparta._9haejodelivery.dto.ProductResponseDto;
import com.sparta._9haejodelivery.dto.ProductUpdateRequestDto;
import com.sparta._9haejodelivery.repository.ProductRepository;
import com.sparta._9haejodelivery.repository.StoreRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final LocalFileService localFileService;
    private final StoreRepository storeRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductCreateRequestDto requestDto, MultipartFile image) throws IOException {
        StoreEntity store = storeRepository.findById(Integer.valueOf(requestDto.getStoreId()))
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        String imagePath = localFileService.saveFile(image);

        ProductEntity product = ProductEntity.builder()
                .store(store)
                .productName(requestDto.getProductName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .image(imagePath)
                .isSoldout(requestDto.getIsSoldout())
                .build();

        ProductEntity savedProduct = productRepository.save(product);
        return ProductResponseDto.from(savedProduct);
    }

    @Transactional
    public ProductResponseDto updateProduct(UUID productId, ProductUpdateRequestDto requestDto) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        product.updateProduct(
                requestDto.getProductName(),
                requestDto.getDescription(),
                requestDto.getPrice(),
                requestDto.getImage(),
                requestDto.getIsSoldout()
        );

        return ProductResponseDto.from(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsByStore(UUID storeId) {
        return productRepository.findAllByStoreId(storeId).stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductDetail(UUID productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return ProductResponseDto.from(product);
    }

    @Transactional
    public void deleteProduct(UUID productId, String username) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        product.markAsDeleted(username);
    }

}
