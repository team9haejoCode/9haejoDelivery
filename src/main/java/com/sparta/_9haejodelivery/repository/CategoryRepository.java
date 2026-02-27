package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}