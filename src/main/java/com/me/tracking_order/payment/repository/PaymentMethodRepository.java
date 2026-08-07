package com.me.tracking_order.payment.repository;

import com.me.tracking_order.payment.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository
        extends JpaRepository<PaymentMethod, String> {

    List<PaymentMethod> findAllByIsDeletedIsFalseOrderByNameAsc();

    Optional<PaymentMethod> findByIdAndIsDeletedIsFalse(String id);
}
