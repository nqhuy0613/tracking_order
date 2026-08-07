package com.me.tracking_order.order.dto.admin.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminOrdersDailySummary {

    private long totalOrders;

    private long confirmedOrders;

    private long pendingOrders;

    public AdminOrdersDailySummary(
            Long totalOrders,
            Long confirmedOrders,
            Long pendingOrders
    ) {
        this.totalOrders =
                totalOrders == null ? 0 : totalOrders;
        this.confirmedOrders =
                confirmedOrders == null ? 0 : confirmedOrders;
        this.pendingOrders =
                pendingOrders == null ? 0 : pendingOrders;
    }
}
