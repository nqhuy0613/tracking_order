package com.me.tracking_order.cart.mapper;

import com.me.tracking_order.cart.dto.response.CartItemResponse;
import com.me.tracking_order.cart.entity.CartItem;
import com.me.tracking_order.catalog.enums.StockStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(
        // map struct sinh ra class implementation
        componentModel = MappingConstants.ComponentModel.SPRING,
        // bao loi neu object chua duoc mapping
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CartItemMapper {

    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "productVariantName", source = "productVariant.name")
    @Mapping(target = "sku", source = "productVariant.sku")
    @Mapping(
            target = "availableStock",
            source = "productVariant.inventory.quantityInStock"
    )
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "stockStatus", ignore = true)
    CartItemResponse toResponse(CartItem cartItem);

    @AfterMapping
    default void calculateLineTotal(
            CartItem cartItem,
            @MappingTarget CartItemResponse response
    ) {
        if (cartItem.getUnitPrice() == null) {
            return;
        }

        BigDecimal lineTotal = cartItem
                .getUnitPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        response.setLineTotal(lineTotal);
    }

    @AfterMapping
    default void getStockStatus(
            CartItem cartItem,
            @MappingTarget CartItemResponse response
    ) {
        int availableStock = response.getAvailableStock();
        if (availableStock <= 0) {
            response.setStockStatus(StockStatus.OUT_OF_STOCK);
        } else if (availableStock <=5) {
            response.setStockStatus(StockStatus.LIMITED_STOCK);
        } else {
            response.setStockStatus(StockStatus.IN_STOCK);
        }
    }
}
