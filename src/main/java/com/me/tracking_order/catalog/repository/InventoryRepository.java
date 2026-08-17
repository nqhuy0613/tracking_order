package com.me.tracking_order.catalog.repository;

import com.me.tracking_order.catalog.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface InventoryRepository
        extends JpaRepository<Inventory, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select inventory
    from Inventory inventory
    join inventory.productVariant variant
    where variant.id in :variantIds
      and inventory.isDeleted = false
    order by inventory.id
""")
    List<Inventory> findAllActiveByVariantIdsForUpdate(
            @Param("variantIds") Collection<String> variantIds
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select i
        from Inventory i
        join i.productVariant pv
        where pv.id = :id 
""")
    Inventory findByVariantIdForUpdate(
            @Param("id") String id);

    List<Inventory> findAllByProductVariantIdInAndIsDeletedFalse(List<String> variantIds);
}
