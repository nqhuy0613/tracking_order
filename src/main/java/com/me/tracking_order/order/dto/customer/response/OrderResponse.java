package com.me.tracking_order.order.dto.customer.response;


import com.me.tracking_order.order.enums.MyOrderStatus;
import com.me.tracking_order.payment.enums.PaymentMethodStatus;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {

    // order
    private String id;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    // payment
    private PaymentMethodStatus paymentMethod;

    // shipment+payment
    private MyOrderStatus status;

    // order item
    private int itemCount;
}
