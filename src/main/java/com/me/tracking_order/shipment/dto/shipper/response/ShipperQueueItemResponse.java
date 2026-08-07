package com.me.tracking_order.shipment.dto.shipper.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipperQueueItemResponse {

    private String id;

    private String trackingNumber;

    private int orderItemCount;
}
