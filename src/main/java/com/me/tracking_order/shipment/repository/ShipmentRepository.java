package com.me.tracking_order.shipment.repository;

import com.me.tracking_order.shipment.entity.Shipment;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment,String> {
    @Query("""
        select s
            from Shipment s
            join s.order o
            where o.isDeleted = false 
              and s.isDeleted = false
              and o.id in :orderIds
              and s.status = :status
""")
    List<Shipment> findByOrderIds(
            @Param("orderIds") List<String> orderIds,
            ShipmentStatus status);

    @Query("""
        select s
        from Shipment s
        join s.order o
        where o.id = :orderId
          and o.isDeleted = false
          and s.isDeleted = false
          and s.status = :status
    """)
    Optional<Shipment> findActiveByOrderIdAndStatus(
            @Param("orderId") String orderId,
            @Param("status") ShipmentStatus status
    );

    @Query("""
        select s
        from Shipment s
        join s.order o
        join o.user u
        where o.id = :orderId
          and o.isDeleted = false
          and s.isDeleted = false
          and u.username = :username
    """)
    Optional<Shipment> findActiveOwnedByOrderId(
            @Param("orderId") String orderId,
            @Param("username") String username
    );

    @Query("""
        select s
        from Shipment s
        join s.order o
        join o.user u
        where o.id = :orderId
          and o.isDeleted = false
          and s.isDeleted = false
          and u.username = :username
          and s.status = :status
    """)
    Optional<Shipment> findActiveOwnedByOrderIdAndStatus(
            @Param("orderId") String orderId,
            @Param("username") String username,
            @Param("status") ShipmentStatus status
    );
}
