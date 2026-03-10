package com.sparta._9haejodelivery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderUpdateAddressRequestDto {
  @NotNull
  @Size(min = 10, max = 100, message = "주소를 상세히 입력해주세요 (10자 이상).")
  private String address;
}
