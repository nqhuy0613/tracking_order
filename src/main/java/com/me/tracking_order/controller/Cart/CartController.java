package com.me.tracking_order.controller.Cart;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.dto.request.UpdateCartItemQuantityRequest;
import com.me.tracking_order.dto.response.CartItemResponse;
import com.me.tracking_order.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PutMapping("/items/{cartItemId}/quantity")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @PathVariable String cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request,
            Authentication authentication
    ) {
        CartItemResponse result = cartService.updateQuantity(
                cartItemId,
                request,
                authentication.getName()
        );

        ApiResponse<CartItemResponse> response = ApiResponse.success(
                HttpStatus.OK,
                "Cart item quantity updated successfully",
                result
        );

        return ResponseEntity.ok(response);
    }
}
