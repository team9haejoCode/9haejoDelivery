package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.PaymentHistory;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, UUID> {
    
}