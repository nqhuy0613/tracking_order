package com.me.tracking_order.cart.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponse {
    private String id;
    private List<CartItemResponse> items;
}
