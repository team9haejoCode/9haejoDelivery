package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryEntity, Long> {
}