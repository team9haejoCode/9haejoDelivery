package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByStoreStoreId(UUID storeId);
}
