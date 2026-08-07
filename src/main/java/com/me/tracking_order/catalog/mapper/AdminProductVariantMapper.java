package com.me.tracking_order.catalog.mapper;

import com.me.tracking_order.catalog.dto.admin.response.AdminGetAllVariantResponse;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.catalog.enums.StockStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AdminProductVariantMapper {

    @Mapping(target = "quantityInStock", ignore = true)
    @Mapping(target = "stockStatus", ignore = true)
    AdminGetAllVariantResponse toResponse(
            ProductVariant productVariant
    );

    @AfterMapping
    default void mapInventoryStatus(
            ProductVariant productVariant,
            @MappingTarget AdminGetAllVariantResponse response
    ) {
        Inventory inventory = productVariant.getInventory();

        int quantityInStock =
                inventory == null || inventory.isDeleted()
                        ? 0
                        : inventory.getQuantityInStock();

        response.setQuantityInStock(quantityInStock);

        if (quantityInStock <= 0) {
            response.setStockStatus(
                    StockStatus.OUT_OF_STOCK
            );
        } else if (quantityInStock <= 5) {
            response.setStockStatus(
                    StockStatus.LIMITED_STOCK
            );
        } else {
            response.setStockStatus(
                    StockStatus.IN_STOCK
            );
        }
    }
}
