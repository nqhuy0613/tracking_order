package com.me.tracking_order.cart.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.cart.dto.request.UpdateCartItemQuantityRequest;
import com.me.tracking_order.cart.dto.response.CartItemResponse;
import com.me.tracking_order.cart.dto.response.CartResponse;
import com.me.tracking_order.cart.entity.Cart;
import com.me.tracking_order.cart.entity.CartItem;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.cart.mapper.CartItemMapper;
import com.me.tracking_order.cart.repository.CartItemRepository;
import com.me.tracking_order.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final CartRepository cartRepository;

    @Transactional
    public CartItemResponse updateQuantity(
            String cartItemId,
            UpdateCartItemQuantityRequest request,
            String username
    ) {
        CartItem cartItem = cartItemRepository
                .findActiveOwnedCartItem(cartItemId, username)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CART_ITEM_NOT_FOUND
                ));

        Inventory inventory = cartItem
                .getProductVariant()
                .getInventory();

        if (inventory == null || inventory.isDeleted()) {
            throw new BusinessException(ErrorCode.PRODUCT_UNAVAILABLE);
        }

        int requestedQuantity = request.getQuantity();

        if (requestedQuantity > inventory.getQuantityInStock()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }

        cartItem.setQuantity(requestedQuantity);

        return cartItemMapper.toResponse(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String username) {
        Cart cart = cartRepository
                .findActiveOwnedCart(username)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CART_NOT_FOUND
                ));

        List<CartItemResponse> items = cart.getCartItems().stream()
                .filter(cartItem -> !cartItem.isDeleted())
                .filter(cartItem -> cartItem.getProductVariant() != null && !cartItem.getProductVariant().isDeleted())
                .filter(cartItem -> {
                    Inventory inventory = cartItem.getProductVariant().getInventory();
                    return inventory != null && !inventory.isDeleted();
                })
                .map(cartItemMapper::toResponse)
                .toList();
        CartResponse cartResponse = new CartResponse();
        cartResponse.setItems(items);
        cartResponse.setId(cart.getId());
        return cartResponse;
    }
}
