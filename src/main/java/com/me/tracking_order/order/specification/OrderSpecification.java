package com.me.tracking_order.order.specification;

import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import jakarta.persistence.criteria.JoinType;
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
