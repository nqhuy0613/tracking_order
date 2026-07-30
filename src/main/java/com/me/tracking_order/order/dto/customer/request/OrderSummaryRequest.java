package com.me.tracking_order.order.dto.customer.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderSummaryRequest {
    private List<String> cartItemIds;

    private String userDiscountId;
}
