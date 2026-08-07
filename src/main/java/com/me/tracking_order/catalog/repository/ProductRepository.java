package com.me.tracking_order.catalog.repository;

import com.me.tracking_order.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    long countByIsDeletedFalse();
}
