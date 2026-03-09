package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Ai;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiLogRepository extends JpaRepository<Ai, UUID> {

}
