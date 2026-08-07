package com.me.tracking_order.order.dto.customer.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReorderItemResponse {

    private String cartItemId;

    private int quantity;
}
