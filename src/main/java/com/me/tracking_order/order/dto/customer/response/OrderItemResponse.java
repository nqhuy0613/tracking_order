package com.me.tracking_order.order.dto.customer.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemResponse {

    private String id;

    private String productVariantId;
    private String productName;
    private String productVariantName;
    private String sku;
    private String image;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
