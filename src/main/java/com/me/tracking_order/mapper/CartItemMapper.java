package com.me.tracking_order.mapper;

import com.me.tracking_order.dto.response.CartItemResponse;
import com.me.tracking_order.entity.CartItem;
import com.me.tracking_order.entity.Inventory;
import com.me.tracking_order.entity.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CartItemMapper {

    private final ModelMapper modelMapper;

    public CartItemResponse toResponse(CartItem cartItem) {
        CartItemResponse response = modelMapper.map(
                cartItem,
                CartItemResponse.class
        );

        ProductVariant variant = cartItem.getProductVariant();
        Inventory inventory = variant.getInventory();

        response.setProductVariantId(variant.getId());
        response.setProductVariantName(variant.getName());
        response.setSku(variant.getSku());

        if (inventory != null) {
            response.setAvailableStock(inventory.getQuantityInStock());
        }

        BigDecimal lineTotal = cartItem
                .getUnitPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        response.setLineTotal(lineTotal);
        return response;
    }
}
