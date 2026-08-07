package com.me.tracking_order.cart.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCartItemResponse {
    private String cartItemId;
    private int quantity;
}
