package com.me.tracking_order.cart.service;

import com.me.tracking_order.cart.dto.request.AddCartItemRequest;
import com.me.tracking_order.cart.dto.response.AddCartItemResponse;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.catalog.repository.ProductVariantRepository;
import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.cart.dto.request.UpdateCartItemQuantityRequest;
import com.me.tracking_order.cart.dto.response.CartItemResponse;
import com.me.tracking_order.cart.dto.response.CartResponse;
import com.me.tracking_order.cart.entity.Cart;
import com.me.tracking_order.cart.entity.CartItem;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.cart.mapper.AddCartItemMapper;
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
    private final AddCartItemMapper addCartItemMapper;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;

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

    @Transactional
    public AddCartItemResponse addItemToCart(
            String variantId,
            AddCartItemRequest request,
            String username
    ) {
        Cart cart = cartRepository
                .findActiveOwnedCart(username)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CART_NOT_FOUND
                ));

        ProductVariant variant = productVariantRepository
                .findActiveForCartById(variantId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCT_UNAVAILABLE
                ));

        Inventory inventory = variant.getInventory();

        if (inventory == null || inventory.isDeleted()) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_UNAVAILABLE
            );
        }

        // lấy cart item trong cart nếu đã có
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item
                        .getProductVariant()
                        .getId()
                        .equals(variantId))
                .findFirst()
                .orElse(null);

        int finalQuantity;

        if (cartItem == null || cartItem.isDeleted()) {
            finalQuantity = request.getQuantity();
        } else {
            finalQuantity = cartItem.getQuantity()
                    + request.getQuantity();
        }

        if (finalQuantity > inventory.getQuantityInStock()) {
            throw new BusinessException(
                    ErrorCode.INSUFFICIENT_STOCK
            );
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductVariant(variant);
        } else {
            cartItem.setDeleted(false);
        }

        cartItem.setQuantity(finalQuantity);
        cartItem.setUnitPrice(variant.getUnitPrice());

        CartItem savedCartItem =
                cartItemRepository.save(cartItem);

        return addCartItemMapper.toResponse(savedCartItem);
    }
}
