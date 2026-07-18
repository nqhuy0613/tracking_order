package com.me.tracking_order.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.dto.request.UpdateCartItemQuantityRequest;
import com.me.tracking_order.dto.response.CartItemResponse;
import com.me.tracking_order.entity.CartItem;
import com.me.tracking_order.entity.Inventory;
import com.me.tracking_order.mapper.CartItemMapper;
import com.me.tracking_order.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

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
}
