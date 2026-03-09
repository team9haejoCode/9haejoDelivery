package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.Review;
import com.sparta._9haejodelivery.domain.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Optional<Review> findByOrder(Order order);

    Page<Review> findAll(@NonNull Pageable pageable);

    @Query("select r from Review r " +
            "join r.order o " +
            "where o.store.storeId=:storeId")

    Slice<Review> findByStoreId(@Param("storeId")UUID storeId, Pageable pageable);

    Slice<Review> findByUser(User user, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r " +
            "join r.order o " +
            "where o.store.storeId=:storeId")
    BigDecimal getAverageRatingByStoreId(UUID storeId);
}
