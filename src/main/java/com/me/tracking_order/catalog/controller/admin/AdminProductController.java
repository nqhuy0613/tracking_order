package com.me.tracking_order.catalog.controller.admin;

import com.me.tracking_order.catalog.dto.admin.request.AdminCreateProductRequest;
import com.me.tracking_order.catalog.dto.admin.request.AdminUpdateProductVariantRequest;
import com.me.tracking_order.catalog.dto.admin.request.AdminVariantFilterRequest;
import com.me.tracking_order.catalog.dto.admin.response.AdminCreateProductResponse;
import com.me.tracking_order.catalog.dto.admin.response.AdminGetAllVariantResponse;
import com.me.tracking_order.catalog.dto.admin.response.AdminProductSummaryResponse;
import com.me.tracking_order.catalog.dto.admin.response.AdminUpdateProductVariantResponse;
import com.me.tracking_order.catalog.service.AdminProductService;
import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.common.response.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/export", produces = "text/csv")
    public void exportProductVariants(
        HttpServletResponse response
    ) throws IOException {
        // set content type, tieng Viet
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        // set header(cac thong tin cua response): filename,...
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String fileName = "product-variants-" + timestamp + ".csv";
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                // dang file, có tên..
                ContentDisposition.attachment()
                        .filename(fileName, StandardCharsets.UTF_8)
                        .build()
                        .toString()
        );

        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        // goi service xu li
        adminProductService.exportProductVariants(
                response.getOutputStream()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<AdminCreateProductResponse>> createProduct(
            @Valid @RequestBody AdminCreateProductRequest request
    ){
        AdminCreateProductResponse result = adminProductService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).
                body(ApiResponse.success(
                   "Product created successfully",
                   result
                ));
    }
}
