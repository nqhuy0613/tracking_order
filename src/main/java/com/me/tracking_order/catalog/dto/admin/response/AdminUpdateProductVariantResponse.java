package com.me.tracking_order.catalog.dto.admin.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdminUpdateProductVariantResponse {

    private String id;

    private String sku;

    private BigDecimal unitPrice;

    private String name;

    private String image;

    private Integer quantityInStock;

    private String description;

    private String categoryName;
}
