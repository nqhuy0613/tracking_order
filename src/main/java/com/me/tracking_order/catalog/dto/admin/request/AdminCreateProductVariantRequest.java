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
public class AdminCreateProductVariantRequest {

    @NotBlank(message = "Variant name must not be blank")
    @Size(
            max = 255,
            message = "Variant name must not exceed 255 characters"
    )
    private String name;

    @NotBlank(message = "SKU must not be blank")
    @Size(
            max = 100,
            message = "SKU must not exceed 100 characters"
    )
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
            message = "Unit price must have at most 13 integer digits and 2 decimal digits"
    )
    private BigDecimal unitPrice;

    @DecimalMin(
            value = "0.00",
            inclusive = false,
            message = "Weight must be greater than 0"
    )
    @Digits(
            integer = 8,
            fraction = 2,
            message = "Weight must have at most 8 integer digits and 2 decimal digits"
    )
    private BigDecimal weight;

    @Size(
            max = 100,
            message = "Color must not exceed 100 characters"
    )
    private String color;

    @Size(
            max = 100,
            message = "Size must not exceed 100 characters"
    )
    private String size;

    @Size(
            max = 500,
            message = "Image URL must not exceed 500 characters"
    )
    private String image;

    @NotNull(message = "Quantity in stock must not be null")
    @Min(
            value = 0,
            message = "Quantity in stock must be greater than or equal to 0"
    )
    private Integer quantityInStock;
}
