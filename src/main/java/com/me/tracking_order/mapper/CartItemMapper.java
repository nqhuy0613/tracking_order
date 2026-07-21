package com.me.tracking_order.mapper;

import com.me.tracking_order.dto.response.CartItemResponse;
import com.me.tracking_order.entity.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
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
}
