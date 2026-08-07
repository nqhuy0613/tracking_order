package com.me.tracking_order.order.dto.admin.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RejectOrderRequest {

    @NotBlank(message = "description is required")
    private String description;
}
