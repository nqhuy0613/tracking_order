package com.me.tracking_order.order.dto.admin.response;

import com.me.tracking_order.shipment.enums.ShipmentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RejectOrderResponse {

    private String orderId;

    private ShipmentStatus status;

    private String description;
}
