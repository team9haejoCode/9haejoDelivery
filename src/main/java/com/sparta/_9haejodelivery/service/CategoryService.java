package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.common.BusinessException;
import com.sparta._9haejodelivery.common.ErrorCode;
import com.sparta._9haejodelivery.domain.Category;
import com.sparta._9haejodelivery.dto.CategoryRequestDto;
import com.sparta._9haejodelivery.dto.CategoryResponseDto;
import com.sparta._9haejodelivery.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        Category category = Category.builder()
                .categoryName(requestDto.getCategoryName())
                .build();
        return new CategoryResponseDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponseDto::new)
                .toList();
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID categoryId, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        category.updateName(requestDto.getCategoryName());
        return new CategoryResponseDto(category);
    }

    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        category.markAsDeleted(null); // TODO: userId
    }
}