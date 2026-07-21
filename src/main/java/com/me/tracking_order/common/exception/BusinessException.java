package com.me.tracking_order.common.exception;

import lombok.Getter;

@Getter

// hàm này để đại dien cho lỗi nghiệp vụ
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        // ke thua constuctor tu class cha
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
