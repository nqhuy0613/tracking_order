package com.me.tracking_order.cart.controller;

import com.me.tracking_order.cart.dto.request.AddCartItemRequest;
import com.me.tracking_order.cart.dto.response.AddCartItemResponse;
import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.cart.dto.request.UpdateCartItemQuantityRequest;
import com.me.tracking_order.cart.dto.response.CartItemResponse;
import com.me.tracking_order.cart.dto.response.CartResponse;
import com.me.tracking_order.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PutMapping("/items/{cartItemId}/quantity")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @PathVariable String cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request
    ) {
        CartItemResponse result = cartService.updateQuantity(
                cartItemId,
                request
        );

        ApiResponse<CartItemResponse> response = ApiResponse.success(
                "Cart item quantity updated successfully",
                result
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        CartResponse result = cartService.getCart();

        ApiResponse<CartResponse> response = ApiResponse.success(
                "Cart retrived successfully",
                result
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/items/{variantId}")
    public ResponseEntity<ApiResponse<AddCartItemResponse>>  addItemToCart(
            @PathVariable String variantId,
            @Valid @RequestBody AddCartItemRequest request
    ){
        AddCartItemResponse result = cartService.addItemToCart(variantId,
                request
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Item added to cart successfully",
                result
        ));
    }
}
