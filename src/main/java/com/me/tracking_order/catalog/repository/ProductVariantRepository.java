package com.me.tracking_order.catalog.repository;

import com.me.tracking_order.catalog.dto.customer.response.FeaturedProductVariantResponse;
import com.me.tracking_order.catalog.entity.ProductVariant;
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
import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, String>,
                JpaSpecificationExecutor<ProductVariant> {

    boolean existsBySkuAndIdNot(
            String sku,
            String productVariantId
    );

    @Override
    @EntityGraph(attributePaths = {
            "inventory"
    })
    Page<ProductVariant> findAll(
            Specification<ProductVariant> specification,
            Pageable pageable
    );

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

    @Query("""
        select pv
        from ProductVariant pv
        join pv.product product
        left join fetch pv.inventory inventory
        where pv.id = :variantId
          and pv.isDeleted = false
          and product.isDeleted = false
    """)
    Optional<ProductVariant> findActiveForCartById(
            @Param("variantId") String variantId
    );

    @Query("""
        select coalesce(
            sum(pv.unitPrice*i.quantityInStock),
            0
        ) 
        from ProductVariant pv
        join pv.inventory i
        join pv.product p
        where pv.isDeleted = false 
          and i.isDeleted = false 
          and p.isDeleted = false 
""")
    BigDecimal getTotalPrice();

    @Query("""
        select count(pv)
        from ProductVariant pv
        join pv.inventory i
        join pv.product p
        where pv.isDeleted = false 
          and i.isDeleted = false 
          and p.isDeleted = false
          and i.quantityInStock >= 1
          and i.quantityInStock <= :max
""")
    long getLowStockVariantcCount(
            @Param("max") long max
    );

    @Query("""
                select pv
                from ProductVariant pv
                left join fetch pv.inventory i
                join fetch pv.product p
                left join fetch p.category c
                where pv.id = :productVariantId
                  and pv.isDeleted = false
                  and p.isDeleted = false
            """)
    Optional<ProductVariant> findActiveById(
            @Param("productVariantId") String productVariantId
    );

    @Query(value = """
                    select new com.me.tracking_order.catalog.dto.customer.response.FeaturedProductVariantResponse(
                    pv.id,
                    pv.name,
                    pv.unitPrice,
                    pv.image,
                    p.brand,
                    coalesce(i.quantityInStock, 0)
                    )
                    from ProductVariant pv 
                    join pv.product p 
                    join pv.orderItems oi 
                    join oi.order o
                    join o.shipment s
                    left join pv.inventory i
                        on i.isDeleted = false
                    where pv.isDeleted = false 
                    and p.isDeleted = false 
                    and o.isDeleted = false 
                    and s.isDeleted = false 
                    and oi.isDeleted = false
                    and s.status = :status
                    group by
                    pv.id,
                    pv.name,
                    pv.unitPrice,
                    p.brand,
                    i.quantityInStock
                    order by sum(oi.quantity) desc, pv.id asc 
""")
    List<FeaturedProductVariantResponse> getFeaturedProductVariants(
            @Param("status") ShipmentStatus deliveredStatus,
            Pageable pageable
    );


}
