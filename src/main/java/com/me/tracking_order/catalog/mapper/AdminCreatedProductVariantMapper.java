package com.me.tracking_order.catalog.mapper;

import com.me.tracking_order.catalog.dto.admin.response.AdminCreatedProductVariantResponse;
import com.me.tracking_order.catalog.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AdminCreatedProductVariantMapper {

    @Mapping(target = "quantityInStock", source = "inventory.quantityInStock")
    AdminCreatedProductVariantResponse toResponse(ProductVariant productVariant);
}
