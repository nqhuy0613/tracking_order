package com.me.tracking_order.repository;

import com.me.tracking_order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    @Query("""
            select ci
            from CartItem ci
            join fetch ci.cart c
            join fetch c.user u
            join fetch ci.productVariant pv
            left join fetch pv.inventory inventory
            where ci.id = :cartItemId
              and u.username = :username
              and ci.isDeleted = false
              and c.isDeleted = false
              and u.isDeleted = false
              and pv.isDeleted = false
            """)
    Optional<CartItem> findActiveOwnedCartItem(
            @Param("cartItemId") String cartItemId,
            @Param("username") String username
    );
}
