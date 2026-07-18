package com.me.tracking_order.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String code;
    private String message;
    private T data;
    private Object errors;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(
            HttpStatus status,
            String message,
            T data
    ) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status.value())
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> error(
            HttpStatus status,
            String code,
            String message

    ) {
        return ApiResponse.<Void>builder()
                .success(false)
                .status(status.value())
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> validationError(
            String message,
            Object errors
    ) {
        return ApiResponse.<Void>builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .code("VALIDATION_ERROR")
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
