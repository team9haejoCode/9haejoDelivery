package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Product;
import com.sparta._9haejodelivery.domain.Store;
import com.sparta._9haejodelivery.dto.ProductCreateRequestDto;
import com.sparta._9haejodelivery.dto.ProductResponseDto;
import com.sparta._9haejodelivery.dto.ProductUpdateRequestDto;
import com.sparta._9haejodelivery.repository.ProductRepository;
import com.sparta._9haejodelivery.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private LocalFileService localFileService;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 생성 성공 테스트")
    void createProduct_Success() throws IOException {
        // given
        UUID storeId = UUID.randomUUID();
        ProductCreateRequestDto requestDto = mock(ProductCreateRequestDto.class);
        given(requestDto.getStoreId()).willReturn(storeId.toString());
        given(requestDto.getProductName()).willReturn("양념 치킨");
        given(requestDto.getDescription()).willReturn("양념");
        given(requestDto.getPrice()).willReturn(19000);
        given(requestDto.getIsSoldout()).willReturn(false);

        MockMultipartFile mockFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "image".getBytes()
        );

        Store mockStore = mock(Store.class);
        given(mockStore.toString()).willReturn(storeId.toString());

        Product mockProduct = mock(Product.class);
        given(mockProduct.getProductId()).willReturn(UUID.randomUUID());
        given(mockProduct.getStore()).willReturn(mockStore);
        given(mockProduct.getProductName()).willReturn("양념 치킨");
        given(mockProduct.getDescription()).willReturn("양념");
        given(mockProduct.getPrice()).willReturn(19000);
        given(mockProduct.getImage()).willReturn("/uploads/test.jpg");
        given(mockProduct.getIsSoldout()).willReturn(false);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(mockStore));
        given(localFileService.saveFile(any())).willReturn("/uploads/test.jpg");
        given(productRepository.save(any(Product.class))).willReturn(mockProduct);

        // when
        ProductResponseDto response = productService.createProduct(requestDto, mockFile);

        // then
        assertNotNull(response);
        assertEquals("양념 치킨", response.getProductName());
        verify(storeRepository).findById(storeId);
        verify(localFileService).saveFile(any());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 수정 성공 테스트")
    void updateProduct_Success() {
        // given
        UUID productId = UUID.randomUUID();
        ProductUpdateRequestDto requestDto = mock(ProductUpdateRequestDto.class);
        given(requestDto.getProductName()).willReturn("후라이드 치킨");
        given(requestDto.getDescription()).willReturn("바삭함");
        given(requestDto.getPrice()).willReturn(18000);
        given(requestDto.getImage()).willReturn(null);
        given(requestDto.getIsSoldout()).willReturn(true);

        Store mockStore = mock(Store.class);
        given(mockStore.toString()).willReturn("store-uuid-string");

        Product mockProduct = mock(Product.class);
        given(mockProduct.getProductId()).willReturn(productId);
        given(mockProduct.getStore()).willReturn(mockStore);
        given(mockProduct.getProductName()).willReturn("후라이드 치킨");
        given(mockProduct.getDescription()).willReturn("바삭함");
        given(mockProduct.getPrice()).willReturn(18000);
        given(mockProduct.getIsSoldout()).willReturn(true);

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct));

        // when
        ProductResponseDto response = productService.updateProduct(productId, requestDto);

        // then
        assertNotNull(response);
        assertEquals("후라이드 치킨", response.getProductName());
        verify(productRepository).findById(productId);
        verify(mockProduct).updateProduct("후라이드 치킨", "바삭함", 18000, null, true);
    }

    @Test
    @DisplayName("상품 전체 조회 성공 테스트")
    void getAllProducts_Success() {
        // given
        Store mockStore = mock(Store.class);
        given(mockStore.toString()).willReturn("store-uuid-string");

        Product mockProduct = mock(Product.class);
        given(mockProduct.getProductId()).willReturn(UUID.randomUUID());
        given(mockProduct.getStore()).willReturn(mockStore);

        given(productRepository.findAll()).willReturn(List.of(mockProduct));

        // when
        List<ProductResponseDto> responses = productService.getAllProducts();

        // then
        assertEquals(1, responses.size());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("매장 별 상품 조회 성공 테스트")
    void getProductsByStore_Success() {
        // given
        UUID storeId = UUID.randomUUID();
        Store mockStore = mock(Store.class);
        given(mockStore.toString()).willReturn(storeId.toString());

        Product mockProduct = mock(Product.class);
        given(mockProduct.getProductId()).willReturn(UUID.randomUUID());
        given(mockProduct.getStore()).willReturn(mockStore);

        given(productRepository.findAllByStoreId(storeId)).willReturn(List.of(mockProduct));

        // when
        List<ProductResponseDto> responses = productService.getProductsByStore(storeId);

        // then
        assertEquals(1, responses.size());
        verify(productRepository).findAllByStoreId(storeId);
    }

    @Test
    @DisplayName("상품 상세 조회 성공 테스트")
    void getProductDetail_Success() {
        // given
        UUID productId = UUID.randomUUID();
        Store mockStore = mock(Store.class);
        given(mockStore.toString()).willReturn("store-uuid-string");

        Product mockProduct = mock(Product.class);
        given(mockProduct.getProductId()).willReturn(productId);
        given(mockProduct.getStore()).willReturn(mockStore);
        given(mockProduct.getProductName()).willReturn("양념 치킨");

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct));

        // when
        ProductResponseDto response = productService.getProductDetail(productId);

        // then
        assertNotNull(response);
        assertEquals("양념 치킨", response.getProductName());
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("상품 삭제 성공 테스트")
    void deleteProduct_Success() {
        // given
        UUID productId = UUID.randomUUID();
        String username = "system_user";

        Product mockProduct = mock(Product.class);
        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct));

        // when
        productService.deleteProduct(productId, username);

        // then
        verify(productRepository).findById(productId);
        verify(mockProduct).markAsDeleted(username);
    }
}