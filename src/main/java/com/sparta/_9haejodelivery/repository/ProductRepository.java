package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findAllByStoreId(UUID storeId);
}
