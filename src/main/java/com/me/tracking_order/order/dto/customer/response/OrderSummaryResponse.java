package com.me.tracking_order.order.dto.customer.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderSummaryResponse {
    private BigDecimal subTotal;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountFee;
}
