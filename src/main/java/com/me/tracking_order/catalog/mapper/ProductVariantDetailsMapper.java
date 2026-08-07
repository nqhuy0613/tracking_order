package com.me.tracking_order.catalog.mapper;

import com.me.tracking_order.catalog.dto.customer.response.ProductVariantDetailsResponse;
import com.me.tracking_order.catalog.entity.Category;
import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.ProductVariant;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductVariantDetailsMapper {

    @Mapping(target = "quantityInStock", ignore = true)
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    ProductVariantDetailsResponse toResponse(ProductVariant productVariant);

    @AfterMapping
    default void afterMapping(
            ProductVariant productVariant,
            @MappingTarget ProductVariantDetailsResponse response) {

        Inventory inventory = productVariant.getInventory();

        int quantityInStock = inventory == null || inventory.isDeleted()
                ? 0 : inventory.getQuantityInStock();

        response.setQuantityInStock(quantityInStock);

        List<String> categories = new ArrayList<>();

        Category category = productVariant.getProduct().getCategory();
        while(category != null && !category.isDeleted()){
            categories.add(category.getName());
                category = category.getParent();
        }
        Collections.reverse(categories);
        String categoryName = String.join("/ ", categories);
        response.setCategoryName(categoryName);
    }
}
