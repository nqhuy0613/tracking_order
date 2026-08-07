package com.me.tracking_order.catalog.repository;

import com.me.tracking_order.catalog.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository
        extends JpaRepository<Inventory, String> {
}
