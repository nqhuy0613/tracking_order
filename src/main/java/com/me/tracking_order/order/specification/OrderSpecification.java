package com.me.tracking_order.order.specification;

import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.order.entity.OrderItem;
import com.me.tracking_order.shipment.entity.Shipment;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> notDeleted(){
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    public static Specification<Order> hasShipmentStatus(ShipmentStatus shipmentStatus){
        return (root, query, cb) -> cb.equal(
                root.join("shipment", JoinType.INNER).get("status"),
                shipmentStatus);
    }

    public static Specification<Order> hasActiveShipmentStatus(
            ShipmentStatus shipmentStatus
    ) {
        return (root, query, cb) -> {
            Join<Order, Shipment> shipment =
                    root.join("shipment", JoinType.INNER);

            return cb.and(
                    cb.isFalse(shipment.get("isDeleted")),
                    cb.equal(
                            shipment.get("status"),
                            shipmentStatus
                    )
            );
        };
    }

    public static Specification<Order> hasInsufficientStock(
            Boolean lowStockOnly
    ) {
        if (lowStockOnly == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, cb) -> {
            Subquery<Integer> subquery =
                    query.subquery(Integer.class);
            Root<OrderItem> orderItem =
                    subquery.from(OrderItem.class);
            Join<OrderItem, ProductVariant> productVariant =
                    orderItem.join(
                            "productVariant",
                            JoinType.INNER
                    );
            Join<ProductVariant, Inventory> inventory =
                    productVariant.join(
                            "inventory",
                            JoinType.LEFT
                    );

            // tạo câu query
            subquery.select(cb.literal(1))
                    .where(
                            cb.equal(
                                    orderItem.get("order"),
                                    root
                            ),
                            cb.isFalse(
                                    orderItem.get("isDeleted")
                            ),
                            cb.or(
                                    cb.isTrue(
                                            productVariant.get(
                                                    "isDeleted"
                                            )
                                    ),
                                    cb.isNull(inventory.get("id")),
                                    cb.isTrue(
                                            inventory.get("isDeleted")
                                    ),
                                    cb.lessThan(
                                            inventory.<Integer>get(
                                                    "quantityInStock"
                                            ),
                                            orderItem.<Integer>get(
                                                    "quantity"
                                            )
                                    )
                            )
                    );

            Predicate insufficientStockExists =
                    cb.exists(subquery);

            // true  → EXISTS: có item thiếu hàng
            // false → NOT EXISTS: không có item thiếu hàng
            return Boolean.TRUE.equals(lowStockOnly)
                    ? insufficientStockExists
                    : cb.not(insufficientStockExists);
        };
    }

    public static Specification<Order> minAmount(BigDecimal amount){
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalAmount"), amount);
    }

    public static Specification<Order> maxAmount(BigDecimal amount){
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalAmount"), amount);
    }

    public static Specification<Order> hasCarrierName(String carrierName){
        return (root, query, cb) -> cb.equal(root.get("carrierName"), carrierName);
    }

    public static Specification<Order> createdinYear(Integer year){
        return (root, query, cb) -> {
            LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0, 0);

            LocalDateTime end = start.plusYears(1);

            return cb.and(cb.greaterThanOrEqualTo(root.get("createdAt"), start),
            cb.lessThanOrEqualTo(root.get("createdAt"), end));
        };
    }


}
