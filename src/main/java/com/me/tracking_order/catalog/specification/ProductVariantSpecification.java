package com.me.tracking_order.catalog.specification;

import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.catalog.enums.StockStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

public final class ProductVariantSpecification {

    private static final int LIMITED_STOCK_THRESHOLD = 5;

    private ProductVariantSpecification() {
    }

    public static Specification<ProductVariant> notDeleted() {
        return (root, query, cb) -> cb.and(
                cb.isFalse(root.get("isDeleted")),
                cb.isFalse(root.get("product").get("isDeleted"))
        );
    }

    public static Specification<ProductVariant> nameContains(
            String name
    ) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("name")),
                "%" + name.trim().toLowerCase(Locale.ROOT) + "%"
        );
    }

    public static Specification<ProductVariant> skuContains(
            String sku
    ) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("sku")),
                "%" + sku.trim().toLowerCase(Locale.ROOT) + "%"
        );
    }

    public static Specification<ProductVariant> minPrice(
            BigDecimal minPrice
    ) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("unitPrice"),
                        minPrice
                );
    }

    public static Specification<ProductVariant> maxPrice(
            BigDecimal maxPrice
    ) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("unitPrice"),
                        maxPrice
                );
    }

    public static Specification<ProductVariant> hasStockStatus(
            StockStatus stockStatus
    ) {
        return (root, query, cb) -> {
            Join<ProductVariant, Inventory> inventory =
                    root.join("inventory", JoinType.LEFT);

            return switch (stockStatus) {
                case OUT_OF_STOCK -> cb.or(
                        cb.isNull(inventory.get("id")),
                        cb.isTrue(inventory.get("isDeleted")),
                        cb.lessThanOrEqualTo(
                                inventory.get("quantityInStock"),
                                0
                        )
                );

                case LIMITED_STOCK -> cb.and(
                        cb.isFalse(inventory.get("isDeleted")),
                        cb.between(
                                inventory.get("quantityInStock"),
                                1,
                                LIMITED_STOCK_THRESHOLD
                        )
                );

                case IN_STOCK -> cb.and(
                        cb.isFalse(inventory.get("isDeleted")),
                        cb.greaterThan(
                                inventory.get("quantityInStock"),
                                LIMITED_STOCK_THRESHOLD
                        )
                );
            };
        };
    }
}
