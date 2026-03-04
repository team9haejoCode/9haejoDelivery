package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Category;
import com.sparta._9haejodelivery.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    Page<Store> findByIsHideFalse(Pageable pageable);

    Page<Store> findByCategoryAndIsHideFalse(Category category, Pageable pageable);

    Page<Store> findByRegionIdAndIsHideFalse(UUID regionId, Pageable pageable);
}