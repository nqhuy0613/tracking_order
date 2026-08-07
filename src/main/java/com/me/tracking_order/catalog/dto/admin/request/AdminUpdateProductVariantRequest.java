package com.me.tracking_order.catalog.dto.admin.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdminUpdateProductVariantRequest {

    @NotBlank(message = "SKU must not be blank")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @NotNull(message = "Unit price must not be null")
    @DecimalMin(
            value = "0.00",
            inclusive = false,
            message = "Unit price must be greater than 0"
    )
    @Digits(
            integer = 13,
            fraction = 2,
            message = "Unit price is invalid"
    )
    private BigDecimal unitPrice;

    @NotBlank(message = "Variant name must not be blank")
    @Size(max = 255, message = "Variant name must not exceed 255 characters")
    private String name;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String image;

    @NotNull(message = "Quantity in stock must not be null")
    @Min(
            value = 0,
            message = "Quantity in stock must be greater than or equal to 0"
    )
    private Integer quantityInStock;

    private String description;

    @NotBlank(message = "Category name must not be blank")
    @Size(max = 255, message = "Category name must not exceed 255 characters")
    private String categoryName;
}
