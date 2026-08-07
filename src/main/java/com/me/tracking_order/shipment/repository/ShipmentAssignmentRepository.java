package com.me.tracking_order.shipment.repository;

import com.me.tracking_order.shipment.dto.shipper.response.ShipperDailySummaryResponse;
import com.me.tracking_order.shipment.entity.ShipmentAssignment;
import com.me.tracking_order.shipment.enums.AssignmentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShipmentAssignmentRepository extends JpaRepository<ShipmentAssignment, String> {

    @Query("""
        select new com.me.tracking_order.shipment.dto.shipper.response.ShipperDailySummaryResponse(
            count(case 
                when sa.status = :deliveredStatus then 1
                end
            ),
            count(case 
                when sa.status = :failedStatus then 1
                end
            )
        )
        from ShipmentAssignment sa
        join sa.shipment sm
        join sa.shipper sp
        where sp.username = :username
          and sp.isDeleted = false
          and sa.isDeleted = false 
          and sm.isDeleted = false
          and sa.finishedAt >= :startAt
          and sa.finishedAt < :endAt
""")
    ShipperDailySummaryResponse getShipperDailySummary(
            @Param("username") String username,
            @Param("deliveredStatus") AssignmentStatus deliveredStatus,
            @Param("failedStatus") AssignmentStatus failedStatus,
            @Param("startAt")LocalDateTime startAt,
            @Param("endAt")LocalDateTime endAt
            );

    @Query("""
        select sa
        from ShipmentAssignment sa
        join fetch sa.shipment s
        join fetch s.order o
        join sa.shipper u
        where u.isDeleted = false 
          and u.username = :username
          and o.isDeleted = false 
          and sa.isDeleted = false
          and s.isDeleted = false
          and sa.status = :status 
""")
    Optional<ShipmentAssignment> getActiveShipmentAssignmentByUsernameAndStatus(
            @Param("username") String username,
            @Param("status") AssignmentStatus status);

    @Query("""
        select sa
        from ShipmentAssignment sa
        join fetch sa.shipment s
        join fetch s.order o
        join sa.shipper u
        where u.isDeleted = false 
          and u.username = :username
          and o.isDeleted = false 
          and sa.isDeleted = false
          and s.isDeleted = false
          and sa.status = :status
          and sa.id = :id
""")
    Optional<ShipmentAssignment> getActiveShipmentAssignmentByUsernameAndStatusAndId(
            @Param("username") String username,
            @Param("status") AssignmentStatus status,
            @Param("id") String id);

    @Query("""
        select distinct sa
        from ShipmentAssignment sa
        join fetch sa.shipment s
        join fetch s.order o
        join fetch o.orderItems oi
        join sa.shipper u
        where u.isDeleted = false 
          and u.username = :username
          and o.isDeleted = false 
          and sa.isDeleted = false
          and s.isDeleted = false
          and oi.isDeleted = false 
          and sa.status = :status
          order by sa.createdAt ASC
""")
    List<ShipmentAssignment> getAllActiveShipmentAssignmentByUsernameAndStatus(
            @Param("username") String username,
            @Param("status") AssignmentStatus status);
}
