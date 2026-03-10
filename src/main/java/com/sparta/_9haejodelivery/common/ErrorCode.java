package com.sparta._9haejodelivery.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    // Store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다."),
    STORE_FILTER_CONFLICT(HttpStatus.BAD_REQUEST, "category와 sigungu는 동시에 사용할 수 없습니다."),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),

    // Region
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "지역을 찾을 수 없습니다."),

    // Auth
    STORE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 매장에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}