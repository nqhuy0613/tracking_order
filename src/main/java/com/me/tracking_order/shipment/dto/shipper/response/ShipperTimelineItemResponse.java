package com.me.tracking_order.shipment.dto.shipper.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ShipperTimelineItemResponse {

    private String id;

    private String status;

    private String title;

    private LocalDateTime createdAt;

    private String shipmentId;

    private String trackingNumber;

}
