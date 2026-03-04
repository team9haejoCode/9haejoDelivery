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
        this.categoryName = categoryName;
    }

    public void updateName(String categoryName) {
        this.categoryName = categoryName;
    }
}