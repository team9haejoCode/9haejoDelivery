package com.sparta._9haejodelivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderCreateRequestDto {
  @NotNull(message = "가게 정보는 필수입니다.")
  private UUID storeId;
  @NotBlank(message = "배달 주소는 필수입니다.")
  @Size(min = 10, max = 100, message = "주소를 상세히 입력해주세요 (10자 이상).")
  private String address;
  @Valid
  @NotEmpty(message = "주문할 상품을 최소 하나 이상 선택해주세요.")
  private List<OrderItemCreateDto> items;

  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  public static class OrderItemCreateDto {
    @NotNull(message = "상품 ID는 필수입니다.")
    private UUID productId;
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "최소 1개 이상 주문해야 합니다.")
    private Integer quantity;
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
    private Integer unitPrice;
  }
}