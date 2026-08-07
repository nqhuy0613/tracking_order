package com.me.tracking_order.catalog.dto.admin.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AdminProductSummaryResponse {

    private BigDecimal totalPrice;

    private long productVariantCount;

    private long lowStockVariantCount;
}
