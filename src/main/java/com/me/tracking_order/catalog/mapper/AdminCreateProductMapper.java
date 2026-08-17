package com.me.tracking_order.catalog.mapper;

import com.me.tracking_order.catalog.dto.admin.response.AdminCreateProductResponse;
import com.me.tracking_order.catalog.dto.admin.response.AdminCreatedProductVariantResponse;
import com.me.tracking_order.catalog.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AdminCreateProductMapper {

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "brand", source = "product.brand")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "variants", source = "variants")
    AdminCreateProductResponse toResponse(
            Product product,
            List<AdminCreatedProductVariantResponse> variants
            );
}
