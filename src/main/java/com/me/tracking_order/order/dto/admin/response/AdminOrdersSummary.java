package com.me.tracking_order.order.dto.admin.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class AdminOrdersSummary {
    private BigDecimal totalRevenue;

    private Long totalOrders;

    private Long pendingOrders;

    private Long shippingOrders;

    private Long failedOrders;

    public AdminOrdersSummary(
            BigDecimal totalRevenue,
            Long totalOrders,
            Long pendingOrders,
            Long shippingOrders,
            Long failedOrders
    ) {
        this.totalRevenue = totalRevenue ==  null ? BigDecimal.ZERO : totalRevenue;
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.shippingOrders = shippingOrders;
        this.failedOrders = failedOrders;
    }
}
