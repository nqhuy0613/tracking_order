package com.me.tracking_order.shipment.dto.customer.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentDetailResponse {

    private String id;

    private String receiverName;

    private String receiverPhone;

    private String shippingProvince;

    private String shippingCommune;

    private String shippingDetailAddress;
}
