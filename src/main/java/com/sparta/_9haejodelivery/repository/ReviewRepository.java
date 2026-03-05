package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.Review;
import com.sparta._9haejodelivery.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Optional<Review> findByOrder(Order order);

    List<Review> findByUser(User user);
}
