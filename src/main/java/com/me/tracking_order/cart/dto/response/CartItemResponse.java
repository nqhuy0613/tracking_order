package com.me.tracking_order.cart.dto.response;

import com.me.tracking_order.catalog.enums.StockStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CartItemResponse {

    private String id;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String productVariantId;
    private String productVariantName;
    private String sku;
    private Integer availableStock;
    private StockStatus stockStatus;
}
