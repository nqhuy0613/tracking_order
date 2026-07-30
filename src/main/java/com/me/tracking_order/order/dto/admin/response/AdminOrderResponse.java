package com.me.tracking_order.order.dto.admin.response;

import com.me.tracking_order.payment.enums.PaymentStatus;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AdminOrderResponse {

    private String id;

    private BigDecimal totalAmount;

    private ShipmentStatus shipmentStatus;

    private LocalDateTime createdAt;

    private String customerName;

    private String carrierName;
}
