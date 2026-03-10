package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.common.BusinessException;
import com.sparta._9haejodelivery.common.ErrorCode;
import com.sparta._9haejodelivery.domain.Product;
import com.sparta._9haejodelivery.domain.Store;
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
        Store store = storeRepository.findById(UUID.fromString(requestDto.getStoreId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        String imagePath = localFileService.saveFile(image);

        Product product = Product.builder()
                .store(store)
                .productName(requestDto.getProductName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .image(imagePath)
                .isSoldout(requestDto.getIsSoldout())
                .build();

        Product savedProduct = productRepository.save(product);
        return ProductResponseDto.from(savedProduct);
    }

    @Transactional
    public ProductResponseDto updateProduct(UUID productId, ProductUpdateRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

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
        return productRepository.findAllByStoreStoreId(storeId).stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductDetail(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponseDto.from(product);
    }

    @Transactional
    public void deleteProduct(UUID productId, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.markAsDeleted(username);
    }
}