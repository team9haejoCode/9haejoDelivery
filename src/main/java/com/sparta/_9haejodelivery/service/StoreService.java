package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.common.BusinessException;
import com.sparta._9haejodelivery.common.ErrorCode;
import com.sparta._9haejodelivery.domain.Category;
import com.sparta._9haejodelivery.domain.Region;
import com.sparta._9haejodelivery.domain.Store;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
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
    public StoreResponseDto createStore(StoreRequestDto requestDto, User owner) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Region region = regionRepository.findById(requestDto.getBcodeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND));

        Store store = Store.builder()
                .storeName(requestDto.getStoreName())
                .category(category)
                .owner(owner)
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
    public Page<StoreResponseDto> getStoresBySigungu(String sigungu, int page, int size, String sortDirection) {
        Pageable pageable = buildPageable(page, size, sortDirection);
        return storeRepository
                .findByRegion_SigunguAndIsHideFalse(sigungu, pageable)
                .map(StoreResponseDto::new);
    }

    @Transactional(readOnly = true)
    public StoreResponseDto getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        return new StoreResponseDto(store);
    }

    @Transactional
    public StoreResponseDto updateStore(UUID storeId, StoreUpdateRequestDto requestDto, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (user.getRole() == UserRole.OWNER &&
                (store.getOwner() == null || !store.getOwner().getUsername().equals(user.getUsername()))) {
            throw new BusinessException(ErrorCode.STORE_ACCESS_DENIED);
        }

        Category category = requestDto.getCategoryId() != null
                ? categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND))
                : null;

        Region region = requestDto.getBcodeId() != null
                ? regionRepository.findById(requestDto.getBcodeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REGION_NOT_FOUND))
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
    public void deleteStore(UUID storeId, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (user.getRole() == UserRole.OWNER &&
                (store.getOwner() == null || !store.getOwner().getUsername().equals(user.getUsername()))) {
            throw new BusinessException(ErrorCode.STORE_ACCESS_DENIED);
        }

        store.markAsDeleted(user.getUsername());
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