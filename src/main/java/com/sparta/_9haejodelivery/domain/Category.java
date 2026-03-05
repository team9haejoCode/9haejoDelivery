package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "P_CATEGORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", columnDefinition = "uuid")
    private UUID categoryId;

    @Column(name = "category_name", length = 50, nullable = false, unique = true)
    private String categoryName;

    @Builder
    public Category(String categoryName) {
        validateName(categoryName);
        this.categoryName = categoryName.toLowerCase();
    }

    public void updateName(String categoryName) {
        validateName(categoryName);
        this.categoryName = categoryName.toLowerCase();
    }

    private void validateName(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("카테고리 이름은 비어 있을 수 없습니다.");
        }
        if (categoryName.length() > 50) {
            throw new IllegalArgumentException("카테고리 이름은 50자를 초과할 수 없습니다.");
        }
    }
}