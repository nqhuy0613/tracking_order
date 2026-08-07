package com.me.tracking_order.catalog.dto.admin.request;

import com.me.tracking_order.catalog.enums.StockStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdminVariantFilterRequest {

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    private StockStatus stockStatus;

    @DecimalMin(
            value = "0.00",
            message = "Minimum price must be greater than or equal to 0"
    )
    private BigDecimal minPrice;

    @DecimalMin(
            value = "0.00",
            message = "Maximum price must be greater than or equal to 0"
    )
    private BigDecimal maxPrice;
}
