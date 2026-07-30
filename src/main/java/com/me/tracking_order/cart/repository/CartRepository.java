package com.me.tracking_order.cart.repository;

import com.me.tracking_order.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends  JpaRepository<Cart, String> {

    @Query("""
    select distinct c
    from Cart c
    join fetch c.user u
    left join fetch c.cartItems ci
    left join fetch ci.productVariant pv
    left join fetch pv.inventory inventory
    where u.username = :username
      and u.isDeleted = false
      and c.isDeleted = false
""")
    Optional<Cart> findActiveOwnedCart(
            @Param("username") String username
    );


}
