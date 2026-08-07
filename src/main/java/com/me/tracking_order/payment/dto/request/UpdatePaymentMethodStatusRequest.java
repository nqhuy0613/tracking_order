package com.me.tracking_order.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePaymentMethodStatusRequest {

    @NotNull(message = "status is required")
    private Boolean status;
}
