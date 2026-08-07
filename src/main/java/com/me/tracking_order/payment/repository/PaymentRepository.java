package com.me.tracking_order.payment.repository;

import com.me.tracking_order.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    @Query("""
        select payment
        from Payment payment
        join fetch payment.paymentMethod paymentMethod
        where payment.order.id in :orderIds
          and payment.isDeleted = false
        order by payment.order.id, payment.createdAt desc
    """)
    List<Payment> findActiveWithMethodByOrderIds(
            @Param("orderIds") List<String> orderIds
    );
}
