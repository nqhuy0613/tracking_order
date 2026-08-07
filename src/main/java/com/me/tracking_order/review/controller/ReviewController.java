package com.me.tracking_order.review.controller;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.review.dto.response.ProductVariantReviewResponse;
import com.me.tracking_order.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product-variant/{id}")
    public ResponseEntity<ApiResponse<ProductVariantReviewResponse>> getProductVariantReviews(
            @PathVariable("id") String id
    ) {
        ProductVariantReviewResponse result = reviewService.getProductVariantReviews(id);

        return ResponseEntity.ok(ApiResponse.success(
                "Product variant reviews retrieved successfully",
                result
        ));
    }
}
