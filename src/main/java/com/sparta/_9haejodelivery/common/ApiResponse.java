package com.sparta._9haejodelivery.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String code;
    private String status;
    private String message;
    private T data;

    @Builder
    private ApiResponse(String code, String status, String message, T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(HttpStatus httpStatus, String message, T data) {
        return ApiResponse.<T>builder()
                .code(String.valueOf(httpStatus.value()))
                .status(httpStatus.name())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(HttpStatus httpStatus, String message) {
        return ApiResponse.<T>builder()
                .code(String.valueOf(httpStatus.value()))
                .status(httpStatus.name())
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> fail(HttpStatus httpStatus, String message) {
        return ApiResponse.<T>builder()
                .code(String.valueOf(httpStatus.value()))
                .status(httpStatus.name())
                .message(message)
                .data(null)
                .build();
    }
}