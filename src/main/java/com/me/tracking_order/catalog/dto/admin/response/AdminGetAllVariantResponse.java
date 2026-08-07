package com.me.tracking_order.catalog.dto.admin.response;

import com.me.tracking_order.catalog.enums.StockStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdminGetAllVariantResponse {

    private String id;

    private String image;

    private String name;

    private String sku;

    private BigDecimal unitPrice;

    private Integer quantityInStock;

    private StockStatus stockStatus;
}
