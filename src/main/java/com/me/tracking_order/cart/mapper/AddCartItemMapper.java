package com.me.tracking_order.cart.mapper;

import com.me.tracking_order.cart.dto.response.AddCartItemResponse;
import com.me.tracking_order.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        // map struct sinh ra class implementation
        componentModel = MappingConstants.ComponentModel.SPRING,
        // bao loi neu object chua duoc mapping
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AddCartItemMapper {

    @Mapping(target = "cartItemId", source = "id")
    AddCartItemResponse toResponse(CartItem cartItem);
}
