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
            String bcodeId
    ) {
        StoreRequestDto dto = new StoreRequestDto();

        ReflectionTestUtils.setField(dto, "storeName", storeName);
        ReflectionTestUtils.setField(dto, "categoryId", categoryId);
        ReflectionTestUtils.setField(dto, "bcodeId", bcodeId);
        ReflectionTestUtils.setField(dto, "address", "201호");
        ReflectionTestUtils.setField(dto, "description", "테스트 설명");
        ReflectionTestUtils.setField(dto, "isHide", false);

        return dto;
    }

    private StoreUpdateRequestDto createUpdateRequestDto(
            String storeName,
            UUID categoryId,
            String bcodeId
    ) {
        StoreUpdateRequestDto dto = new StoreUpdateRequestDto();

        ReflectionTestUtils.setField(dto, "storeName", storeName);
        ReflectionTestUtils.setField(dto, "categoryId", categoryId);
        ReflectionTestUtils.setField(dto, "bcodeId", bcodeId);
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
        String bcodeId = "1111010100";

        StoreRequestDto requestDto = createRequestDto(storeName, categoryId, bcodeId);

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        Region region = Region.builder()
                .bcodeId(bcodeId)
                .sigungu("서울특별시 종로구")
                .bcode("청운동")
                .build();

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(bcodeId))
                .thenReturn(Optional.of(region));

        when(storeRepository.save(any(Store.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        StoreResponseDto response = storeService.createStore(requestDto);

        // then
        assertEquals(storeName, response.getStoreName());
        verify(categoryRepository).findById(categoryId);
        verify(regionRepository).findById(bcodeId);
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 생성 실패 - 카테고리 없음")
    void createStore_fail_category_not_found() {
        // given
        String storeName = "맛있는 식당";
        UUID categoryId = UUID.randomUUID();
        String bcodeId = "1111010100";

        StoreRequestDto requestDto = createRequestDto(storeName, categoryId, bcodeId);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
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

        Region region = Region.builder()
                .bcodeId("1111010100")
                .sigungu("서울특별시 종로구")
                .bcode("청운동")
                .build();

        Store store = Store.builder()
                .storeName("맛있는 식당")
                .category(category)
                .region(region)
                .build();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        // when & then
        storeService.getStore(storeId);
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

        Region region = Region.builder()
                .bcodeId("1111010100")
                .sigungu("서울특별시 종로구")
                .bcode("청운동")
                .build();

        Store store1 = Store.builder()
                .storeName("맛1")
                .category(category)
                .region(region)
                .isHide(false)
                .build();

        Store store2 = Store.builder()
                .storeName("맛2")
                .category(category)
                .region(region)
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

        Region region = Region.builder()
                .bcodeId("1111010100")
                .sigungu("서울특별시 종로구")
                .bcode("청운동")
                .build();

        Store store1 = Store.builder()
                .storeName("맛1")
                .category(category)
                .region(region)
                .isHide(false)
                .build();

        Store store2 = Store.builder()
                .storeName("맛2")
                .category(category)
                .region(region)
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
    @DisplayName("가게 생성 실패 - 지역 없음")
    void createStore_fail_region_not_found() {
        // given
        UUID categoryId = UUID.randomUUID();
        String bcodeId = "9999999999";

        StoreRequestDto requestDto = createRequestDto("맛있는 식당", categoryId, bcodeId);

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(bcodeId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                storeService.createStore(requestDto));
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 단건 조회 실패 - 매장 없음")
    void getStore_fail_not_found() {
        // given
        UUID storeId = UUID.randomUUID();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                storeService.getStore(storeId));
    }

    @Test
    @DisplayName("시/군/구별 가게 조회 성공")
    void getStoresBySigungu_success() {
        // given
        String sigungu = "서울특별시 종로구";
        int page = 0;
        int size = 10;
        String sortDirection = "ASC";

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        Region region = Region.builder()
                .bcodeId("1111010100")
                .sigungu(sigungu)
                .bcode("청운동")
                .build();

        Store store1 = Store.builder()
                .storeName("맛1")
                .category(category)
                .region(region)
                .isHide(false)
                .build();

        Page<Store> storePage = new PageImpl<>(List.of(store1));

        when(storeRepository.findByRegion_SigunguAndIsHideFalse(eq(sigungu), any(Pageable.class)))
                .thenReturn(storePage);

        // when
        Page<StoreResponseDto> result =
                storeService.getStoresBySigungu(sigungu, page, size, sortDirection);

        // then
        assertEquals(1, result.getContent().size());
        assertEquals("맛1", result.getContent().get(0).getStoreName());
        verify(storeRepository).findByRegion_SigunguAndIsHideFalse(eq(sigungu), any(Pageable.class));
    }

    @Test
    @DisplayName("가게 전체 조회 - 유효하지 않은 size는 10으로 보정")
    void getStores_invalidSize_defaultTo10() {
        // given
        int page = 0;
        int invalidSize = 7;
        String sortDirection = "ASC";

        when(storeRepository.findByIsHideFalse(any(Pageable.class)))
                .thenReturn(Page.empty());

        // when
        storeService.getStores(page, invalidSize, sortDirection);

        // then
        verify(storeRepository).findByIsHideFalse(argThat(pageable ->
                pageable.getPageSize() == 10
        ));
    }

    @Test
    @DisplayName("가게 전체 조회 - 유효하지 않은 정렬 방향은 DESC로 보정")
    void getStores_invalidSortDirection_defaultToDesc() {
        // given
        when(storeRepository.findByIsHideFalse(any(Pageable.class)))
                .thenReturn(Page.empty());

        // when
        storeService.getStores(0, 10, "INVALID");

        // then
        verify(storeRepository).findByIsHideFalse(argThat(pageable ->
                pageable.getSort().getOrderFor("createdAt").getDirection().isDescending()
        ));
    }

    @Test
    @DisplayName("가게 수정 성공")
    void updateStore_success() {
        // given
        UUID storeId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String bcodeId = "1111010100";

        Store store = Store.builder()
                .storeName("맛있는 식당")
                .build();

        StoreUpdateRequestDto requestDto = createUpdateRequestDto("짱맛있는 식당", categoryId, bcodeId);

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        Region region = Region.builder()
                .bcodeId(bcodeId)
                .sigungu("서울특별시 종로구")
                .bcode("청운동")
                .build();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(bcodeId))
                .thenReturn(Optional.of(region));

        // when
        storeService.updateStore(storeId, requestDto);

        // then
        verify(storeRepository).findById(storeId);
        assertEquals("짱맛있는 식당", store.getStoreName());
    }

    @Test
    @DisplayName("가게 수정 실패 - 매장 없음")
    void updateStore_fail_store_not_found() {
        // given
        UUID storeId = UUID.randomUUID();
        StoreUpdateRequestDto requestDto = createUpdateRequestDto("짱맛있는 식당", UUID.randomUUID(), "1111010100");

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                storeService.updateStore(storeId, requestDto));
    }

    @Test
    @DisplayName("가게 수정 실패 - 카테고리 없음")
    void updateStore_fail_category_not_found() {
        // given
        UUID storeId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Store store = Store.builder()
                .storeName("맛있는 식당")
                .build();

        StoreUpdateRequestDto requestDto = createUpdateRequestDto("짱맛있는 식당", categoryId, null);

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                storeService.updateStore(storeId, requestDto));
    }

    @Test
    @DisplayName("가게 수정 실패 - 지역 없음")
    void updateStore_fail_region_not_found() {
        // given
        UUID storeId = UUID.randomUUID();
        String bcodeId = "9999999999";

        Store store = Store.builder()
                .storeName("맛있는 식당")
                .build();

        StoreUpdateRequestDto requestDto = createUpdateRequestDto("짱맛있는 식당", null, bcodeId);

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        when(regionRepository.findById(bcodeId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                storeService.updateStore(storeId, requestDto));
    }

    @Test
    @DisplayName("가게 수정 - categoryId, bcodeId가 null이면 각 레포지토리 조회 안 함")
    void updateStore_partial_skip_category_and_region() {
        // given
        UUID storeId = UUID.randomUUID();

        Category category = Category.builder()
                .categoryName("한식")
                .build();

        Region region = Region.builder()
                .bcodeId("1111010100")
                .sigungu("서울특별시 종로구")
                .bcode("청운동")
                .build();

        Store store = Store.builder()
                .storeName("맛있는 식당")
                .category(category)
                .region(region)
                .build();

        StoreUpdateRequestDto requestDto = createUpdateRequestDto("짱맛있는 식당", null, null);

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        // when
        storeService.updateStore(storeId, requestDto);

        // then
        verify(categoryRepository, never()).findById(any());
        verify(regionRepository, never()).findById(any());
        assertEquals("짱맛있는 식당", store.getStoreName());
    }

    @Test
    @DisplayName("가게 삭제 성공")
    void deleteStore_success() {
        // given
        UUID storeId = UUID.randomUUID();

        Store store = Store.builder()
                .storeName("맛있는 식당")
                .build();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        // when
        storeService.deleteStore(storeId);

        // then
        verify(storeRepository).findById(storeId);
        assertNotNull(store.getDeletedAt());
    }

    @Test
    @DisplayName("가게 삭제 실패 - 매장 없음")
    void deleteStore_fail_not_found() {
        // given
        UUID storeId = UUID.randomUUID();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                storeService.deleteStore(storeId));
    }

}