package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
