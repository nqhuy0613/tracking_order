package com.me.tracking_order.shipment.repository;

import com.me.tracking_order.shipment.entity.TrackingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface TrackingLogRepository extends JpaRepository<TrackingLog, String> {

    @Query("""
        select tl
        from TrackingLog tl
        join tl.shipment s
        join s.order o
        join o.user u
        where o.id = :orderId
          and u.username = :username
          and tl.isDeleted = false 
          and s.isDeleted = false 
          and o.isDeleted = false 
          and u.isDeleted = false 
        order by tl.createdAt desc 
        
""")
    List<TrackingLog> findActiveOwnedTrackingLog (
            @Param("username") String username,
            @Param("orderId") String orderId);


    @Query("""
        select tl
        from TrackingLog tl
        join fetch tl.shipment s
        join fetch s.order o
        where tl.createdBy = :username
          and tl.isDeleted = false 
          and s.isDeleted = false 
          and o.isDeleted = false
          and tl.createdAt >= :startAt
          and tl.createdAt < :endAt 
        order by tl.createdAt desc 
        
""")
    List<TrackingLog> findActiveShipperOwnedTrackingLog (
            @Param("username") String username,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt")  LocalDateTime endAt);
}
