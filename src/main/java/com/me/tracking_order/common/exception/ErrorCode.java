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

    INVALID_PRICE_RANGE(
            HttpStatus.BAD_REQUEST,
            "INVALID_PRICE_RANGE",
            "Invalid price range"
    ),

    RETURN_REQUEST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RETURN_REQUEST_NOT_FOUND",
            "Return request not found"
    ),

    RETURN_REQUEST_NOT_VALID(
            HttpStatus.NOT_FOUND,
            "RETURN_REQUEST_NOT_VALID",
            "Return request not valid"
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
    ),

    INVALID_CART_ITEM_SELECTION(
            HttpStatus.BAD_REQUEST,
            "INVALID_CART_ITEM_SELECTION",
            "One or more selected cart items do not exist, are unavailable, or do not belong to your cart"
    ),

    INVALID_ORDER_ITEM_SELECTION(
            HttpStatus.BAD_REQUEST,
            "INVALID_ORDER_ITEM_SELECTION",
            "Invalid order item"
    ),

    DISCOUNT_NOT_AVAILABLE(
            HttpStatus.BAD_REQUEST,
            "DISCOUNT_NOT_AVAILABLE",
            "Discount not available"
    ),

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND",
            "User not found"
    ),

    ORDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ORDER_NOT_FOUND",
            "Order not found"
    ),

    CART_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "CART_NOT_FOUND",
        "Cart not found"
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
