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

    ROLE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ROLE_NOT_FOUND",
            "Role not found"
    ),

    IN_PROGRESS_ORDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "IN_PROGRESS_ORDER_NOT_FOUND",
            "In progress order not found"
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

    SHIPMENT_ASSIGNMENT_NOT_VALID(
            HttpStatus.NOT_FOUND,
            "SHIPMENT_ASSIGNMENT_NOT_VALID",
            "Shipment assignment not valid"
    ),

    PRODUCT_UNAVAILABLE(
            HttpStatus.BAD_REQUEST,
            "PRODUCT_UNAVAILABLE",
            "Product is currently unavailable"
    ),

    DUPLICATE_ORDER_ITEM(
            HttpStatus.CONFLICT,
            "DUPLICATE_ORDER_ITEM",
            "Duplicate order item found"
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

    INVALID_ORDER_SELECTION(
            HttpStatus.BAD_REQUEST,
            "INVALID_ORDER_SELECTION",
            "Invalid order selection"
    ),

    ORDER_ITEM_ALREADY_REVIEWED(
            HttpStatus.BAD_REQUEST,
            "ORDER_ITEM_ALREADY_REVIEWED",
            "Order item already reviewed"
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

    CARRIER_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "CARRIER_ALREADY_EXISTS",
            "Carrier already exists"
    ),

    USERNAME_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            "USERNAME_ALREADY_EXISTS",
            "Username already exists"
    ),

    PHONE_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            "PHONE_ALREADY_EXISTS",
            "Phone already exists"
    ),

    EMAIL_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            "EMAIL_ALREADY_EXISTS",
            "Email already exists"
    ),

    PASSWORD_NOT_MATCH(
            HttpStatus.BAD_REQUEST,
            "PASSWORD_NOT_MATCH",
            "Password does not match"
    ),

    ORDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ORDER_NOT_FOUND",
            "Order not found"
    ),

    PRODUCT_VARIANT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PRODUCT_VARIANT_NOT_FOUND",
            "Product variant not found"
    ),

    CARRIER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CARRIER_NOT_FOUND",
            "Carrier not found"
    ),

    PAYMENT_METHOD_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PAYMENT_METHOD_NOT_FOUND",
            "Payment method not found"
    ),

    PRODUCT_VARIANT_SKU_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "PRODUCT_VARIANT_SKU_ALREADY_EXISTS",
            "Product variant SKU already exists"
    ),

    PRODUCT_VARIANT_SKU_MUST_UNIQUE(
            HttpStatus.CONFLICT,
            "PRODUCT_VARIANT_SKU_MUST_UNIQUE",
            "Product variant SKU must unique"
    ),

    CATEGORY_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CATEGORY_NOT_FOUND",
            "Category not found"
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
