package com.me.tracking_order.order.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BulkConfirmOrderRequest {

    @NotEmpty
    List<@NotBlank String> orderIds;
}
