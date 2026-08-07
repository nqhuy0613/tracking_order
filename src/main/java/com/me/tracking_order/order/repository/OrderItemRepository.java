package com.me.tracking_order.order.repository;

import com.me.tracking_order.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    @Query("""
        select oi
        from OrderItem oi
        join fetch oi.productVariant pv
        left join fetch pv.inventory inventory
        where oi.order.id in :orderIds
          and oi.isDeleted = false
    """)
    List<OrderItem> findActiveWithVariantAndInventoryByOrderIds(
            @Param("orderIds") List<String> orderIds
    );

    int countByIsDeletedFalseAndOrder_Id(String orderId);

    @Query("""
        select oi
        from OrderItem oi
        join fetch oi.productVariant pv
        join fetch pv.product p
        join fetch pv.inventory i
        where oi.order.id = :orderId
          and oi.isDeleted = false
          and p.isDeleted = false 
          and i.isDeleted = false
          and pv.isDeleted = false
    """)
    List<OrderItem> findActiveByOrderId(
            @Param("orderId") String orderId
    );

    List<OrderItem> findByIsDeletedFalseAndOrder_Id(String orderId);

    List<OrderItem> findByIsDeletedFalseAndOrder_IdIn(Collection<String> orderIds);
}
