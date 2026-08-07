package com.me.tracking_order.shipment.repository;

import com.me.tracking_order.shipment.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarrierRepository extends JpaRepository<Carrier, String> {
    List<Carrier> findAllByIsDeletedIsFalseOrderByNameAsc();

    Optional<Carrier> findByIdAndIsDeletedIsFalse(String id);

    boolean existsByNameIgnoreCase(String name);

}
