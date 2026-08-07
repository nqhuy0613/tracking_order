package com.me.tracking_order.catalog.dto.customer.response;

import com.me.tracking_order.catalog.enums.StockStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FeaturedProductVariantResponse {

    private String id;

    private String name;

    private BigDecimal unitPrice;

    private String image;

    private String brand;

    private StockStatus stockStatus;

    public FeaturedProductVariantResponse(
            String id,
            String name,
            BigDecimal unitPrice,
            String image,
            String brand,
            Integer quantityInStock
    ) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.image = image;
        this.brand = brand;

        if (quantityInStock == null || quantityInStock <= 0) {
            this.stockStatus = StockStatus.OUT_OF_STOCK;
        } else if (quantityInStock < 5) {
            this.stockStatus = StockStatus.LIMITED_STOCK;
        } else {
            this.stockStatus = StockStatus.IN_STOCK;
        }
    }
}
