package com.me.tracking_order.catalog.dto.admin.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminCreateProductResponse {

    private String id;

    private String name;

    private String brand;

    private String description;

    private String categoryId;

    private String categoryName;

    private List<AdminCreatedProductVariantResponse> variants;
}
