package com.me.tracking_order.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    // code là unique message có thể trùng
    private boolean success;
    private String code;
    private String message;
    private T data;
    private Object errors;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> error(
            String code,
            String message

    ) {
        return ApiResponse.<Void>builder()
                .success(false)
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
                .code("VALIDATION_ERROR")
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
