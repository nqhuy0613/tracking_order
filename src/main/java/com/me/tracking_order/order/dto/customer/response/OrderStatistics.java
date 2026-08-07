package com.me.tracking_order.order.dto.customer.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatistics {

    private long shippingOrder;

    private long deliveredOrder;

    public OrderStatistics(Long shippingOrder, Long deliveredOrder) {
        this.shippingOrder = shippingOrder;
        this.deliveredOrder = deliveredOrder;
    }
}
