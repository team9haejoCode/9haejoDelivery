package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.*;
import com.sparta._9haejodelivery.global.security.UserDetailsImpl;
import com.sparta._9haejodelivery.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Order API", description = "주문 관리 및 조회 관련 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  /**
   * 1. 주문 생성 API
   */
  @Operation(summary = "주문 생성", description = "고객 및 관리자가 새로운 주문을 생성합니다. (OWNER 제외)")
  @PreAuthorize("!hasRole('OWNER')")
  @PostMapping
  public ResponseEntity<ApiResponse<OrderCreateResponseDto>> createOrder(
      @Valid
      @RequestBody
      OrderCreateRequestDto requestDto,
      @AuthenticationPrincipal
      UserDetailsImpl userDetails
  ) {
    OrderCreateResponseDto data = orderService.createOrder(requestDto, userDetails.getUsername());
    return ResponseEntity.status(HttpStatus.CREATED)
                         .body(ApiResponse.success(HttpStatus.CREATED, "주문이 성공적으로 생성되었습니다.", data));
  }

  /**
   * 2. 주문 단건 상세 조회 API
   * 권한: 인증된 모든 유저 (상세 로직은 Service에서 본인 확인)
   */
  @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다.")
  @GetMapping("/{orderId}")
  public ResponseEntity<ApiResponse<OrderDetailsResponseDto>> getOrder(
      @PathVariable
      UUID orderId,
      @AuthenticationPrincipal
      UserDetailsImpl userDetails
  ) {
    OrderDetailsResponseDto data = orderService.getOrder(orderId, userDetails.getUsername());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "주문 상세 정보 조회를 성공했습니다.", data));
  }

  /**
   * 3. 주문 내역 검색 (Slice)
   */
  @Operation(summary = "주문 내역 검색 (Slice)", description = "무한 스크롤 방식의 주문 내역 조회를 수행합니다.")
  @GetMapping("/slice")
  public ResponseEntity<ApiResponse<Slice<OrderSearchResponseDto>>> getOrdersSlice(
      @Valid
      @ModelAttribute
      OrderSearchRequestDto requestDto,
      @AuthenticationPrincipal
      UserDetailsImpl userDetails,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    Pageable validatedPageable = getValidatedPageable(pageable);
    Slice<OrderSearchResponseDto> data = orderService.getOrdersSlice(userDetails, requestDto, validatedPageable);
    return ResponseEntity.status(HttpStatus.OK)
                         .body(ApiResponse.success(HttpStatus.OK, "주문 내역(Slice) 조회를 성공했습니다.", data));
  }

  /**
   * 4. 전체 주문 목록 조회 API (Page)
   */

  @Operation(summary = "전체 주문 목록 조회 (Page)", description = "관리자용 페이징 방식의 전체 주문 내역 조회를 수행합니다.")
  @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
  @GetMapping
  public ResponseEntity<ApiResponse<Page<OrderSearchResponseDto>>> getOrdersPage(
      @Valid
      @ModelAttribute
      OrderSearchRequestDto requestDto,
      @AuthenticationPrincipal
      UserDetailsImpl userDetails,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    Pageable validatedPageable = getValidatedPageable(pageable);
    Page<OrderSearchResponseDto> data = orderService.getOrdersPage(userDetails, requestDto, validatedPageable);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "전체 주문 목록 조회를 성공했습니다.", data));
  }

  /**
   * 5. 주문 상태 변경 API
   */
  @Operation(summary = "주문 상태 변경", description = "주문의 상태(접수 완료, 배달 중 등)를 변경합니다.")
  @PatchMapping("/{orderId}/status")
  public ResponseEntity<ApiResponse<OrderUpdateResponseDto>> updateOrderStatus(
      @PathVariable
      UUID orderId,
      @Valid
      @RequestBody
      OrderUpdateStatusRequestDto requestDto,
      @AuthenticationPrincipal
      UserDetailsImpl userDetails
  ) {
    OrderUpdateResponseDto data = orderService.updateOrderStatus(orderId, requestDto, userDetails);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "주문 상태가 변경되었습니다.", data));
  }

  /**
   * 6. 주문 주소 변경 API
   */
  @Operation(summary = "주문 주소 변경", description = "배달 주소를 변경합니다. (고객/관리자만 가능, 점주 불가)")
  @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'MASTER')")
  @PatchMapping("/{orderId}/address")
  public ResponseEntity<ApiResponse<OrderUpdateResponseDto>> updateOrderAddress(
      @PathVariable
      UUID orderId,
      @Valid
      @RequestBody
      OrderUpdateAddressRequestDto requestDto,
      @AuthenticationPrincipal
      UserDetailsImpl userDetails
  ) {
    OrderUpdateResponseDto data = orderService.updateOrderAddress(orderId, requestDto, userDetails);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "배달 주소가 변경되었습니다.", data));
  }

  /**
   * 7. 주문 삭제(취소) API
   */
  @Operation(summary = "주문 삭제(취소)", description = "관리자 권한으로 주문을 삭제 처리합니다. (Soft Delete)")
  @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
  @DeleteMapping("/{orderId}")
  public ResponseEntity<ApiResponse<OrderDeleteResponseDto>> deleteOrder(
      @PathVariable
      UUID orderId,
      @AuthenticationPrincipal
      UserDetailsImpl userDetails
  ) {
    OrderDeleteResponseDto data = orderService.deleteOrder(orderId, userDetails.getUsername());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "주문 취소가 완료되었습니다.", data));
  }

  private Pageable getValidatedPageable(Pageable pageable) {
    int size = pageable.getPageSize();
    // 10, 30, 50이 아니면 기본값 10으로 고정
    if (size != 10 && size != 30 && size != 50) {
      size = 10;
    }

    // 기존의 페이지 번호와 정렬 조건을 유지하면서 사이즈만 변경하여 새 객체 생성
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

}