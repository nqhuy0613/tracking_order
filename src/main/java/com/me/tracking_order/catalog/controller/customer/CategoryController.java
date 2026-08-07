package com.me.tracking_order.catalog.controller.customer;

import com.me.tracking_order.catalog.dto.customer.response.CategoryResponse;
import com.me.tracking_order.catalog.service.CategoryService;
import com.me.tracking_order.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> result = categoryService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.success(
                "Categories retrieved successfully",
                result
        ));
    }


}
