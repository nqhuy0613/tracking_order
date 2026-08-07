package com.me.tracking_order.catalog.controller.admin;

import com.me.tracking_order.catalog.dto.admin.request.AdminUpdateProductVariantRequest;
import com.me.tracking_order.catalog.dto.admin.request.AdminVariantFilterRequest;
import com.me.tracking_order.catalog.dto.admin.response.AdminGetAllVariantResponse;
import com.me.tracking_order.catalog.dto.admin.response.AdminProductSummaryResponse;
import com.me.tracking_order.catalog.dto.admin.response.AdminUpdateProductVariantResponse;
import com.me.tracking_order.catalog.service.AdminProductService;
import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.common.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AdminProductSummaryResponse>> getAdminProductSummary() {

        AdminProductSummaryResponse result = adminProductService.getAdminProductSummary();

        return ResponseEntity.ok(ApiResponse.success(
               "Product summary retrieved successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/product-variant/{id}")
    public ResponseEntity<ApiResponse<AdminUpdateProductVariantResponse>> updateProductVariant(
            @PathVariable String id,
            @Valid @RequestBody AdminUpdateProductVariantRequest request) {

        AdminUpdateProductVariantResponse result = adminProductService.updateProductVariant(request,id);

        return ResponseEntity.ok(ApiResponse.success(
                "Product variant updated successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminGetAllVariantResponse>>> getAllProductVariants(
            @Valid @ModelAttribute AdminVariantFilterRequest request,
            @Min(1) @RequestParam(defaultValue = "1")Integer pageNumber,
            @Min(1) @RequestParam(defaultValue = "3") Integer pageSize
            ) {

        PageResponse<AdminGetAllVariantResponse> result =
                adminProductService.getAllProductVariants(
                        request,
                        pageNumber,
                        pageSize
                );

        return ResponseEntity.ok(ApiResponse.success(
                "Product variants retrieved successfully",
                result
        ));
    }
}
