package com.me.tracking_order.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CART_ITEM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CART_ITEM_NOT_FOUND",
            "Cart item not found"
    ),

    PRODUCT_UNAVAILABLE(
            HttpStatus.BAD_REQUEST,
            "PRODUCT_UNAVAILABLE",
            "Product is currently unavailable"
    ),

    INSUFFICIENT_STOCK(
            HttpStatus.BAD_REQUEST,
            "INSUFFICIENT_STOCK",
            "Requested quantity exceeds available stock"
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
