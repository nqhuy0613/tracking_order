package com.me.tracking_order.order.dto.customer.response;

import com.me.tracking_order.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CreateOrderResponse {
    private String orderId;

    private BigDecimal subTotal;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountFee;

    private PaymentStatus paymentStatus;
}
