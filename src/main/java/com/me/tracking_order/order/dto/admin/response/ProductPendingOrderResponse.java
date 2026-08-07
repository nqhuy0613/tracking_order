package com.me.tracking_order.order.dto.admin.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductPendingOrderResponse {

    // product variant
    private BigDecimal unitPrice;

    private String name;

    // order item
    private Integer quantity;

    // inventory
    private Integer quantityInStock;

}
