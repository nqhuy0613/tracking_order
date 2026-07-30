package com.me.tracking_order.order.dto.admin.request;

import com.me.tracking_order.shipment.enums.ShipmentStatus;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdminOrderFilterRequest {

    @DecimalMin("0.0")
    private BigDecimal minAmount;

    @DecimalMin("0.0")
    private BigDecimal maxAmount;

    private ShipmentStatus  shipmentStatus;

    private String carrierName;

    private Integer year;
}
