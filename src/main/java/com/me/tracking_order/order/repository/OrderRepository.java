package com.me.tracking_order.order.repository;

import com.me.tracking_order.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String >, JpaSpecificationExecutor<Order> {

    @Query("""
        select distinct o
            from Order o
            join fetch o.user u
            left join fetch o.orderItems oi
            left join fetch oi.productVariant pv
            left join fetch pv.product p
            left join fetch o.shipment s
            left join fetch s.carrier c
            where o.id = :orderId
              and u.username = :username
              and o.isDeleted = false
              and u.isDeleted = false
        
""")
    Optional<Order> findActiveOwnedOrder(
            @Param("username") String username,
            @Param("orderId") String orderId);

    @Override
    @EntityGraph(attributePaths = {
            "shipment",
            "shipment.carrier"
    })
    Page<Order> findAll(
            Specification<Order> specification,
            Pageable pageable
    );
}
