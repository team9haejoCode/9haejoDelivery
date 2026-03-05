package com.sparta._9haejodelivery.dto;

import com.sparta._9haejodelivery.domain.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderSearchRequestDto {

  private UUID storeId;         // 특정 가게의 주문만 조회
  private String username;      // 특정 사용자의 주문만 조회(관리자용)
  private OrderStatus status;   // 특정 상태(예: 배달중)의 주문만 조회

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;  // 조회 시작일

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;    // 조회 종료일

}