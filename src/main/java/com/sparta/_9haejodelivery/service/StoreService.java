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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private static final List<Integer> ALLOWED_SIZES = List.of(10, 30, 50);

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final RegionRepository regionRepository;

    @Transactional
    public StoreResponseDto createStore(StoreRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        Region region = regionRepository.findById(requestDto.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("지역을 찾을 수 없습니다."));

        Store store = Store.builder()
                .storeName(requestDto.getStoreName())
                .category(category)
                .region(region)
                .address(requestDto.getAddress())
                .description(requestDto.getDescription())
                .isHide(requestDto.getIsHide())
                .build();

        return new StoreResponseDto(storeRepository.save(store));
    }

    @Transactional(readOnly = true)
    public Page<StoreResponseDto> getStores(int page, int size, String sortDirection) {
        Pageable pageable = buildPageable(page, size, sortDirection);
        return storeRepository.findByIsHideFalse(pageable).map(StoreResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponseDto> getStoresByCategory(String categoryName, int page, int size, String sortDirection) {
        Pageable pageable = buildPageable(page, size, sortDirection);
        return storeRepository
                .findByCategory_CategoryNameAndIsHideFalse(categoryName, pageable)
                .map(StoreResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponseDto> getStoresByDistrict(String district, int page, int size, String sortDirection) {
        Pageable pageable = buildPageable(page, size, sortDirection);
        return storeRepository
                .findByRegion_DistrictAndIsHideFalse(district, pageable)
                .map(StoreResponseDto::new);
    }

    @Transactional(readOnly = true)
    public StoreResponseDto getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));
        return new StoreResponseDto(store);
    }

    @Transactional
    public StoreResponseDto updateStore(UUID storeId, StoreUpdateRequestDto requestDto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        Category category = requestDto.getCategoryId() != null
                ? categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."))
                : null;

        Region region = requestDto.getRegionId() != null
                ? regionRepository.findById(requestDto.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("지역을 찾을 수 없습니다."))
                : null;

        store.updateStore(
                requestDto.getStoreName(),
                category,
                region,
                requestDto.getAddress(),
                requestDto.getDescription(),
                requestDto.getIsHide()
        );
        return new StoreResponseDto(store);
    }

    @Transactional
    public void deleteStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));
        store.markAsDeleted(null); // TODO: userId
    }

    private Pageable buildPageable(int page, int size, String sortDirection) {
        int validatedSize = ALLOWED_SIZES.contains(size) ? size : 10;
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.DESC;
        }
        Sort sort = Sort.by(direction, "createdAt");
        return PageRequest.of(page, validatedSize, sort);
    }
}