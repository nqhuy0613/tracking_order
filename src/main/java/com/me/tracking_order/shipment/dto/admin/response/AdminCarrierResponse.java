package com.me.tracking_order.shipment.dto.admin.response;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminCarrierResponse {

    private String id;

    private String name;

    private String description;

    private boolean isEnabled;
}
