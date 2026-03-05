package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findAllByOrderId(UUID orderId);
}