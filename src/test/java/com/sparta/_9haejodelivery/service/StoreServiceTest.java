package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Category;
import com.sparta._9haejodelivery.domain.Region;
import com.sparta._9haejodelivery.domain.Store;
import com.sparta._9haejodelivery.dto.StoreRequestDto;
import com.sparta._9haejodelivery.dto.StoreResponseDto;
import com.sparta._9haejodelivery.dto.StoreUpdateRequestDto;
import com.sparta._9haejodelivery.repository.CategoryRepository;
import com.sparta._9haejodelivery.repository.RegionRepository;
import com.sparta._9haejodelivery.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private StoreService storeService;

    private StoreRequestDto createRequestDto(
            String storeName,
            UUID categoryId,
            UUID regionId
    ) {
        StoreRequestDto dto = new StoreRequestDto();

        ReflectionTestUtils.setField(dto, "storeName", storeName);
        ReflectionTestUtils.setField(dto, "categoryId", categoryId);
        ReflectionTestUtils.setField(dto, "regionId", regionId);
        ReflectionTestUtils.setField(dto, "address", "201호");
        ReflectionTestUtils.setField(dto, "description", "테스트 설명");
        ReflectionTestUtils.setField(dto, "isHide", false);

        return dto;
    }

    private StoreUpdateRequestDto createUpdateRequestDto(
            String storeName,
            UUID categoryId,
            UUID regionId
    ) {
        StoreUpdateRequestDto dto = new StoreUpdateRequestDto();

        ReflectionTestUtils.setField(dto, "storeName", storeName);
        ReflectionTestUtils.setField(dto, "categoryId", categoryId);
        ReflectionTestUtils.setField(dto, "regionId", regionId);
        ReflectionTestUtils.setField(dto, "address", "202호");
        ReflectionTestUtils.setField(dto, "description", "테스트 설명2");
        ReflectionTestUtils.setField(dto, "isHide", false);

        return dto;
    }

    @Test
    @DisplayName("가게 생성 성공")
    void createStore_success() {
        // given
        String storeName = "맛있는 식당";
        UUID categoryId = UUID.randomUUID();
        UUID regionId = UUID.randomUUID();

        StoreRequestDto requestDto = createRequestDto(storeName, categoryId, regionId);

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        Region region = Region.builder()
                .regionId(regionId)
                .zonecode("06234")
                .sigungu("성남시 분당구")
                .bcode("백현동")
                .build();

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        when(storeRepository.save(any(Store.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        StoreResponseDto response = storeService.createStore(requestDto);

        // then
        assertEquals(storeName, response.getStoreName());
        verify(categoryRepository).findById(categoryId);
        verify(regionRepository).findById(regionId);
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 생성 실패 - 카테고리 없음")
    void createStore_fail_category_not_found() {
        // given
        String storeName = "맛있는 식당";
        UUID categoryId = UUID.randomUUID();
        UUID regionId = UUID.randomUUID();

        StoreRequestDto requestDto = createRequestDto(storeName, categoryId, regionId);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                storeService.createStore(requestDto));
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 조회 성공")
    void getStore_success() {
        // given
        UUID storeId = UUID.randomUUID();

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        Store store = Store.builder()
                .storeName("맛있는 식당")
                .category(category)
                .build();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        // when
        StoreResponseDto response = storeService.getStore(storeId);

        // then
        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("가게 전체 조회 성공")
    void getStores_success() {
        // given
        int page = 0;
        int size = 10;
        String sortDirection = "ASC";

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        Store store1 = Store.builder()
                .storeName("맛1")
                .category(category)
                .isHide(false)
                .build();

        Store store2 = Store.builder()
                .storeName("맛2")
                .category(category)
                .isHide(false)
                .build();

        Page<Store> storePage =
                new PageImpl<>(List.of(store1, store2));

        when(storeRepository.findByIsHideFalse(any(Pageable.class)))
                .thenReturn(storePage);

        // when
        Page<StoreResponseDto> result =
                storeService.getStores(page, size, sortDirection);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals("맛1", result.getContent().get(0).getStoreName());
        assertEquals("한식", result.getContent().get(0).getCategoryName());

        verify(storeRepository)
                .findByIsHideFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("카테고리별 가게 조회 성공")
    void getStoresByCategory_success() {
        // given
        String categoryName = "한식";
        int page = 0;
        int size = 10;
        String sortDirection = "ASC";

        Category category = Category.builder()
                .categoryName(categoryName)
                .build();

        Store store1 = Store.builder()
                .storeName("맛1")
                .category(category)
                .isHide(false)
                .build();

        Store store2 = Store.builder()
                .storeName("맛2")
                .category(category)
                .isHide(false)
                .build();

        Page<Store> storePage =
                new PageImpl<>(List.of(store1, store2));

        when(storeRepository.findByCategory_CategoryNameAndIsHideFalse(
                eq(categoryName),
                any(Pageable.class)))
                .thenReturn(storePage);

        // when
        Page<StoreResponseDto> result =
                storeService.getStoresByCategory(categoryName, page, size, sortDirection);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals("맛1", result.getContent().get(0).getStoreName());
        assertEquals("한식", result.getContent().get(0).getCategoryName());

        verify(storeRepository)
                .findByCategory_CategoryNameAndIsHideFalse(eq(categoryName), any(Pageable.class));
    }

    @Test
    @DisplayName("가게 수정 성공")
    void updateStore_success() {
        // given
        UUID storeId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID regionId = UUID.randomUUID();

        Store store = Store.builder()
                .storeName("맛있는 식당")
                .build();

        StoreUpdateRequestDto requestDto = createUpdateRequestDto("짱맛있는 식당", categoryId, regionId);

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        Region region = Region.builder()
                .regionId(regionId)
                .zonecode("06234")
                .sigungu("성남시 분당구")
                .bcode("백현동")
                .build();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(regionId))
                .thenReturn(Optional.of(region));

        // when
        StoreResponseDto responseDto = storeService.updateStore(storeId, requestDto);

        // then
        verify(storeRepository).findById(storeId);
        assertEquals("짱맛있는 식당", store.getStoreName());
    }

}