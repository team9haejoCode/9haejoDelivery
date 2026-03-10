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

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "이미 탈퇴 처리된 사용자입니다."),
    ALREADY_LOGOUT_USER(HttpStatus.BAD_REQUEST, "이미 로그아웃 상태이거나 토큰이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
    USER_INFO_MISMATCH(HttpStatus.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다. 다시 로그인해주세요."),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}