package com.sparta._9haejodelivery.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // Store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다."),
    STORE_FILTER_CONFLICT(HttpStatus.BAD_REQUEST, "category와 sigungu는 동시에 사용할 수 없습니다."),
    STORE_CLOSED(HttpStatus.BAD_REQUEST, "매장이 열리지 않았습니다."),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),

    // Region
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "지역을 찾을 수 없습니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_SOLDOUT(HttpStatus.BAD_REQUEST, "선택한 상품이 품절 상태입니다."),

    // File
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기는 10MB를 초과할 수 없습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않은 파일 형식입니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 확장자가 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),

    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),

    ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "이미 탈퇴 처리된 사용자입니다."),

    ALREADY_LOGOUT_USER(HttpStatus.BAD_REQUEST, "이미 로그아웃 상태이거나 토큰이 존재하지 않습니다."),

    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),

    USER_INFO_MISMATCH(HttpStatus.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다. 다시 로그인해주세요."),

    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // Order (CUD & Authority)
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),

    ORDER_NOT_YOURS(HttpStatus.FORBIDDEN, "본인의 주문만 관리할 수 있습니다."),

    ORDER_NOT_YOUR_STORE(HttpStatus.FORBIDDEN, "해당 가게의 주문을 관리할 권한이 없습니다."),

    ORDER_STATUS_NOT_CHANGEABLE(HttpStatus.BAD_REQUEST, "완료되거나 취소된 주문은 상태를 변경할 수 없습니다."),

    ORDER_ADDRESS_NOT_CHANGEABLE(HttpStatus.BAD_REQUEST, "주문 접수 상태일 때만 주소를 변경할 수 있습니다."),

    ORDER_DELETE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "주문 삭제는 관리자만 가능합니다."),

    ORDER_CANCEL_TIMEOUT_EXCEEDED(HttpStatus.NOT_ACCEPTABLE, "주문 후 5분이 지나 취소할 수 없습니다."),

    ORDER_ADDRESS_NOT_CHANGEABLE_BY_OWNER(HttpStatus.BAD_REQUEST, "주문 주소 변경은 점주가 할 수 없습니다."),

    //Review
    ALREADY_EXIST_REVIEW(HttpStatus.BAD_REQUEST, "이미 존재하는 리뷰입니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리뷰를 찾을 수 없습니다."),
    NOT_AUTHOR(HttpStatus.FORBIDDEN, "리뷰 작성자만 수정/삭제할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}