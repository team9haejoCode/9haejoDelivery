package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    @Query(value = "SELECT s FROM Store s JOIN FETCH s.category JOIN FETCH s.region WHERE s.isHide = false",
           countQuery = "SELECT COUNT(s) FROM Store s WHERE s.isHide = false")
    Page<Store> findByIsHideFalse(Pageable pageable);

    @Query(value = "SELECT s FROM Store s JOIN FETCH s.category JOIN FETCH s.region WHERE s.category.categoryName = :categoryName AND s.isHide = false",
           countQuery = "SELECT COUNT(s) FROM Store s WHERE s.category.categoryName = :categoryName AND s.isHide = false")
    Page<Store> findByCategory_CategoryNameAndIsHideFalse(@Param("categoryName") String categoryName, Pageable pageable);

    @Query(value = "SELECT s FROM Store s JOIN FETCH s.category JOIN FETCH s.region WHERE s.region.sigungu = :sigungu AND s.isHide = false",
           countQuery = "SELECT COUNT(s) FROM Store s WHERE s.region.sigungu = :sigungu AND s.isHide = false")
    Page<Store> findByRegion_SigunguAndIsHideFalse(@Param("sigungu") String sigungu, Pageable pageable);
}