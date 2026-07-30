package com.me.tracking_order.catalog.repository;

import com.me.tracking_order.catalog.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {

    @Query("""
                select pv
                from ProductVariant pv
                left join fetch pv.inventory inventory
                where pv.id = :productVariantId
                  and pv.isDeleted = false
            """)
    Optional<ProductVariant> findActiveWithInventoryById(
            @Param("productVariantId") String productVariantId
    );

}
