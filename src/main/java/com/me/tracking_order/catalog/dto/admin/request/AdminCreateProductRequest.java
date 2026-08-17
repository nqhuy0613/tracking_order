package com.me.tracking_order.catalog.dto.admin.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminCreateProductRequest {

    @NotBlank(message = "Product name must not be blank")
    @Size(
            max = 255,
            message = "Product name must not exceed 255 characters"
    )
    private String name;

    @Size(
            max = 255,
            message = "Brand must not exceed 255 characters"
    )
    private String brand;

    private String description;

    @NotBlank(message = "Category ID must not be blank")
    private String categoryId;

    @Valid
    @NotEmpty(message = "Product must have at least one variant")
    private List<
            @NotNull(message = "Product variant must not be null")
            AdminCreateProductVariantRequest> variants;
}
