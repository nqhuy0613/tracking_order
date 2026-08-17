package com.me.tracking_order.catalog.mapper;

import com.me.tracking_order.catalog.dto.admin.response.ProductVariantCsvRow;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.catalog.enums.StockStatus;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductVariantCsvRowMapper {
    @Mapping(target = "id", source = "id")

    @Mapping(
            target = "name",
            source = "name",
            qualifiedByName = "csvSafe"
    )

    @Mapping(
            target = "sku",
            source = "sku",
            qualifiedByName = "csvSafe"
    )

    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "quantityInStock", ignore = true)
    @Mapping(target = "stockStatus", ignore = true)
    ProductVariantCsvRow toRow(ProductVariant productVariant);

    @AfterMapping
    default void afterMapping(
            ProductVariant productVariant,
            @MappingTarget ProductVariantCsvRow productVariantCsvRow) {

        Inventory inventory = productVariant.getInventory();

        int quantityInStock =
                inventory == null || inventory.isDeleted()
                ? 0
                : inventory.getQuantityInStock();

        productVariantCsvRow.setQuantityInStock(quantityInStock);

        if(quantityInStock <= 0) {
            productVariantCsvRow.setStockStatus(StockStatus.OUT_OF_STOCK);
        } else if (quantityInStock <= 5) {
            productVariantCsvRow.setStockStatus(StockStatus.LIMITED_STOCK);

        } else {
            productVariantCsvRow.setStockStatus(StockStatus.IN_STOCK);
        }
    }

    @Named("csvSafe")
    default String csvSafe(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // xóa kí tự trắng ở đầu
        String normalizedValue = value.stripLeading();

        // neu bat dau bang cac ki tu ben duoi-> them dau ', de tranh hieu la cong thuc
        if (!normalizedValue.isEmpty()
                && "=+-@".indexOf(normalizedValue.charAt(0)) >= 0) {
            return "'" + value;
        }

        return value;
    }
}
