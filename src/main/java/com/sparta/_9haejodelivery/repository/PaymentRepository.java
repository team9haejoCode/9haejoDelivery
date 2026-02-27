package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.PaymentEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    List<PaymentEntity> findAllByOrderId(Long orderId);

}