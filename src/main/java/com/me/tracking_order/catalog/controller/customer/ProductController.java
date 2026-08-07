package com.me.tracking_order.catalog.controller.customer;

import com.me.tracking_order.catalog.dto.customer.response.FeaturedProductVariantResponse;
import com.me.tracking_order.catalog.dto.customer.response.ProductVariantDetailsResponse;
import com.me.tracking_order.catalog.service.ProductVariantService;
import com.me.tracking_order.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductVariantService productVariantService;

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<FeaturedProductVariantResponse>>> getFeaturedProductVariants() {
        List<FeaturedProductVariantResponse> result = productVariantService.getFeaturedProductVariants();

        return ResponseEntity.ok(ApiResponse.success(
                "Featured product variants retrieved successfully",
                result
        ));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<ProductVariantDetailsResponse>> getProductVariantDetails(
            @PathVariable String id
    ) {
        ProductVariantDetailsResponse result = productVariantService.getProductVariantDetails(id);

        return ResponseEntity.ok(ApiResponse.success(
                "Product variant details retrieved successfully",
                result
        ));
    }
}
