package com.me.tracking_order.shipment.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCarrierRequest {
    @NotBlank(message = "name is required")
    @Size(max = 255, message = "name is less than 255 characters")
    private String name;

    private String description;

    @NotNull(message = "isEnabled is required")
    private Boolean isEnabled;
}
