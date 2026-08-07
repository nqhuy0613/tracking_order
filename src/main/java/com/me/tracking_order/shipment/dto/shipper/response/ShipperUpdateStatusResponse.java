package com.me.tracking_order.shipment.dto.shipper.response;

import com.me.tracking_order.shipment.enums.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipperUpdateStatusResponse {

    private String id;

    private AssignmentStatus status;

    private String description;
}
