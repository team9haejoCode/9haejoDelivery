package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByCategoryName(String categoryName);
}