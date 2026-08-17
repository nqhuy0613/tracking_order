package com.me.tracking_order.catalog.dto.admin.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class AdminProductSummaryResponse {

    private BigDecimal totalPrice;

    private long productVariantCount;

    private long lowStockVariantCount;

    public AdminProductSummaryResponse(
            BigDecimal totalPrice,

            long productVariantCount,

            long lowStockVariantCount
    ) {
       this.totalPrice = totalPrice == null ? BigDecimal.ZERO : totalPrice;
       this.productVariantCount = productVariantCount;
       this.lowStockVariantCount = lowStockVariantCount;
    }
}
