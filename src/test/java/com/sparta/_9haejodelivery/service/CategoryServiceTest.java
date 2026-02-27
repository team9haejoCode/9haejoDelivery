package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.CategoryEntity;
import com.sparta._9haejodelivery.dto.CategoryRequestDto;
import com.sparta._9haejodelivery.dto.CategoryResponseDto;
import com.sparta._9haejodelivery.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryRequestDto createRequestDto(String name) {
        CategoryRequestDto dto = new CategoryRequestDto();
        ReflectionTestUtils.setField(dto, "categoryName", name);
        return dto;
    }

    private CategoryEntity createEntity(String name) {
        CategoryEntity entity = CategoryEntity.builder()
                .categoryName(name)
                .build();
        ReflectionTestUtils.setField(entity, "categoryId", UUID.randomUUID());
        return entity;
    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategory_success() {
        // given
        CategoryRequestDto requestDto = createRequestDto("한식");
        CategoryEntity savedEntity = createEntity("한식");
        given(categoryRepository.save(any(CategoryEntity.class))).willReturn(savedEntity);

        // when
        CategoryResponseDto result = categoryService.createCategory(requestDto);

        // then
        assertThat(result.getCategoryName()).isEqualTo("한식");
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    @DisplayName("카테고리 전체 조회 성공")
    void getCategories_success() {
        // given
        List<CategoryEntity> entities = List.of(
                createEntity("한식"),
                createEntity("중식")
        );
        given(categoryRepository.findAll()).willReturn(entities);

        // when
        List<CategoryResponseDto> result = categoryService.getCategories();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategoryName()).isEqualTo("한식");
        assertThat(result.get(1).getCategoryName()).isEqualTo("중식");
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void updateCategory_success() {
        // given
        UUID categoryId = UUID.randomUUID();
        CategoryEntity entity = createEntity("한식");
        CategoryRequestDto requestDto = createRequestDto("중식");
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(entity));

        // when
        CategoryResponseDto result = categoryService.updateCategory(categoryId, requestDto);

        // then
        assertThat(result.getCategoryName()).isEqualTo("중식");
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 존재하지 않는 카테고리")
    void updateCategory_notFound() {
        // given
        UUID categoryId = UUID.randomUUID();
        CategoryRequestDto requestDto = createRequestDto("중식");
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_success() {
        // given
        UUID categoryId = UUID.randomUUID();
        CategoryEntity entity = createEntity("한식");
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(entity));

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository).delete(entity);
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 존재하지 않는 카테고리")
    void deleteCategory_notFound() {
        // given
        UUID categoryId = UUID.randomUUID();
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }
}