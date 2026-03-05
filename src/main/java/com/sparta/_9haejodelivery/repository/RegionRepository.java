package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {
}
