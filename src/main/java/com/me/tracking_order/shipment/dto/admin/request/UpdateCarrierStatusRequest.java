package com.me.tracking_order.shipment.dto.admin.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCarrierStatusRequest {
    @NotNull(message = "status is required")
    private Boolean status;
}
