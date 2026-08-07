package com.me.tracking_order.order.repository;

import com.me.tracking_order.order.dto.admin.response.AdminOrdersDailySummary;
import com.me.tracking_order.order.dto.admin.response.AdminOrdersSummary;
import com.me.tracking_order.order.dto.customer.response.OrderStatistics;
import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.payment.enums.PaymentStatus;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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


    long count();


    @Query("""
        select new com.me.tracking_order.order.dto.admin.response.AdminOrdersSummary(
            sum(case
                when s.status = :deliveredStatus then o.totalAmount
                else null
            end),
            count(o),
            count(case
                when s.status = :pendingStatus then 1
            end),
            count(case
                when s.status = :shippingStatus then 1
            end),
            count(case
                when s.status = :failedStatus then 1
            end)
        )
        from Order o
        join o.shipment s
        where o.isDeleted = false 
""")
    AdminOrdersSummary getAdminOrderSummary(
            @Param("deliveredStatus") ShipmentStatus deliveredStatus,
            @Param("pendingStatus") ShipmentStatus pendingStatus,
            @Param("shippingStatus") ShipmentStatus shippingStatus,
            @Param("failedStatus") ShipmentStatus failedStatus
    );

    @Query("""
        select new com.me.tracking_order.order.dto.admin.response.AdminOrdersDailySummary(
            count(o),
            count(case
                when s.status != :confirmedStatus then 1
                else null
            end),
            count(case
                when s.status = :pendingStatus then 1
                else null
            end)
        )
        from Order o
        left join o.shipment s
            on s.isDeleted = false
        where o.isDeleted = false
          and o.createdAt >= :startTime
          and o.createdAt < :endTime
    """)
    AdminOrdersDailySummary
    getDailySummary(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("confirmedStatus")
            ShipmentStatus confirmedStatus,
            @Param("pendingStatus")
            ShipmentStatus pendingStatus
    );

    @Query("""
                    select o
                    from Order o
                    left join o.shipment s 
                    join o.user u
                    where o.isDeleted = false 
                      and u.username = :username
                      and s.status in :status
                      and u.isDeleted = false
            """)
    List<Order> getMyActiveOrdersByShipmentStatus(
            @Param("username") String username,
            @Param("status") List<ShipmentStatus> status
    );

    @Query("""
                    select o
                    from Order o
                    left join o.shipment s
                    join o.user u
                    where o.isDeleted = false 
                      and o.paymentStatus in :status
                      and u.username = :username
                      and u.isDeleted = false
            """)
    List<Order> getMyActiveOrdersByPaymentStatus(
            @Param("username") String username,
            @Param("status") List<PaymentStatus> status
    );

    @EntityGraph(attributePaths = "shipment")
    List<Order> findAllByUser_UsernameAndUser_IsDeletedFalseAndIsDeletedFalseOrderByCreatedAtDesc(
            String username
    );

    @Query("""
        select new com.me.tracking_order.order.dto.customer.response.OrderStatistics(
            count(case
                when s.status = :shippingStatus then 1
                else null
            end),
            count(case
                when s.status = :deliveredStatus then 1
                else null
            end)
        )
        from Order o
        join o.user u
        join o.shipment s
        where o.isDeleted = false
          and u.isDeleted = false
          and s.isDeleted = false
          and u.username = :username
    """)
    OrderStatistics getOrderStatistics(
            @Param("username") String username,
            @Param("shippingStatus") ShipmentStatus shippingStatus,
            @Param("deliveredStatus") ShipmentStatus deliveredStatus
    );

    @Query("""
        select o
        from Order o
        join o.user u
        join o.shipment s
        where o.isDeleted = false 
          and u.isDeleted = false 
          and u.username = :username
          and o.id = :id
          and s.isDeleted = false 
          and s.status = :status
""")
    Optional<Order> getActiveOwnedDeliveredOrder(
            @Param("username") String username,
            @Param("id") String id,
            @Param("status") ShipmentStatus status
    );
}
