package com.me.tracking_order.order.dto.customer.request;

import com.me.tracking_order.order.enums.OrderSource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Order source is required")
    private OrderSource source;

    @Size(max = 100, message = "Too many cart items")
    private List<@NotBlank String> cartItemIds;

    @Valid
    private BuyNowItemRequest buyNowItem;

    private String userDiscountId;
}
