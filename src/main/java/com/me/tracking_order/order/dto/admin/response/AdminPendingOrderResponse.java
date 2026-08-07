package com.me.tracking_order.order.dto.admin.response;

import com.me.tracking_order.payment.enums.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminPendingOrderResponse {

    // order
    private String orderId;

    private BigDecimal totalAmount;

    private PaymentStatus paymentStatus;

    private long elapsedMinutes;

    //shipment
    private String receiverName;

    private String address;

    // payment
    private String paymentMethod;

    //order items
    private boolean isLowStock;

    private List<ProductPendingOrderResponse> productPendingOrders;
}
