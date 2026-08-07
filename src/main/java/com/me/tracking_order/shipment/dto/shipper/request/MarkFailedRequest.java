package com.me.tracking_order.shipment.dto.shipper.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MarkFailedRequest {

    @NotBlank(message = "Reason is required")
    private String reason;
}
