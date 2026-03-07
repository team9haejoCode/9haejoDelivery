package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Page<Payment> findAllByOrderId(UUID orderId, Pageable pageable);
}