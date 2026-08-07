package com.me.tracking_order.catalog.dto.customer.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
public class ProductVariantDetailsResponse {

    private String id;

    private String sku;

    private BigDecimal unitPrice;

    private String name;

    private String image;

    private int quantityInStock;

    private String description;

    private int reviewCount;

    private String categoryName;
}
