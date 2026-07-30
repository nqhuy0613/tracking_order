package com.me.tracking_order.order.mapper;

import com.me.tracking_order.order.dto.customer.response.OrderItemResponse;

import com.me.tracking_order.order.entity.OrderItem;
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
public interface OrderItemMapper {

    @Mapping(
            target = "productVariantId",
            source = "productVariant.id"
    )
    @Mapping(
            target = "productName",
            source = "productVariant.product.name"
    )
    @Mapping(
            target = "productVariantName",
            source = "productVariant.name"
    )
    @Mapping(
            target = "sku",
            source = "productVariant.sku"
    )
    @Mapping(
            target = "image",
            source = "productVariant.image"
    )
    @Mapping(target = "lineTotal", ignore = true)
    OrderItemResponse toResponse(OrderItem orderItem);

    @AfterMapping
    default void calculateLineTotal(
            OrderItem orderItem,
            @MappingTarget OrderItemResponse response
    ) {
        BigDecimal lineTotal = orderItem
                .getUnitPrice()
                .multiply(BigDecimal.valueOf(
                        orderItem.getQuantity()
                ));

        response.setLineTotal(lineTotal);
    }
}
