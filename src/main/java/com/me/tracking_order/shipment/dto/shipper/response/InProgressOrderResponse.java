package com.me.tracking_order.shipment.dto.shipper.response;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InProgressOrderResponse {

    private String id;

    private String trackingNumber;

    private String receiverName;

    private String receiverPhone;

    private String address;

    private int orderItemCounts;
}
