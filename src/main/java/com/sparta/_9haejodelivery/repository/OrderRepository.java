package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.Order;
import com.sparta._9haejodelivery.domain.OrderStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  @Query("SELECT DISTINCT o FROM Order o " + "JOIN FETCH o.user u " + "JOIN FETCH o.store s " +
         "JOIN FETCH o.orderItemEntities oi " + "JOIN FETCH oi.product p " + "WHERE o.orderId = :orderId")
  Optional<Order> findByIdWithAllDetails(
      @Param("orderId")
      UUID orderId
  );

  /**
   * 특정 사용자의 주문 항목들을 Slice로 조회
   * 고객: 이전 주문 내역 확인, 점주: 현재 주문 내역 확인 등
   */
  // Pageable을 넘기면 JPA가 내부적으로 limit, offset 쿼리를 생성
  // Store만 미리 긁어오기 (성능 최적화)
  @Query("SELECT o FROM Order o " + "JOIN FETCH o.user " + "JOIN FETCH o.store s " +
         "WHERE (:username IS NULL OR o.user.username = :username) " +
         "AND (:storeId IS NULL OR o.store.storeId = :storeId) " + "AND o.status = COALESCE(:status, o.status)")
  Slice<Order> searchOrdersSlice(
      @Param("username")
      String username,
      @Param("storeId")
      UUID storeId,
      @Param("status")
      OrderStatus status, Pageable pageable
  );

  /**
   * 특정 사용자의 주문 항목들을 Page로 조회
   * 관리자: 특정 유저, 가게의 전체 주문 리스트 조회
   */
  @Query("SELECT o FROM Order o " + "JOIN FETCH o.user " + "JOIN FETCH o.store s " +
         "WHERE (:username IS NULL OR o.user.username = :username) " +
         "AND (:storeId IS NULL OR o.store.storeId = :storeId) " + "AND o.status = COALESCE(:status, o.status)")
  Page<Order> searchOrdersPage(
      @Param("username")
      String username,
      @Param("storeId")
      UUID storeId,
      @Param("status")
      OrderStatus status, Pageable pageable
  );

  // 비관적 락(3초 제한 - 데드락 방지)
  // validate 시 필요한 user/store 추가 쿼리는 1:1 관계이므로 감안
  // 락을 걸고 join fetch 시 user/store row 락 문제
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
  @Query("SELECT o FROM Order o WHERE o.orderId = :id")
  Optional<Order> findByIdForUpdate(
      @Param("id")
      UUID id
  );
}