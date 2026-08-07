package com.me.tracking_order.shipment.dto.shipper.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipperDailySummaryResponse {

    private long deliveredShipments;

    private long failedShipments;

    private float efficiency;

    public ShipperDailySummaryResponse(
            long deliveredShipments, long failedShipments
    ){
        this.deliveredShipments = deliveredShipments;
        this.failedShipments = failedShipments;
        long total = deliveredShipments + failedShipments;

        this.efficiency = total == 0
                ? 0
                : (float) deliveredShipments * 100 / total;
    }
}
