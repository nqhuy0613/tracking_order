package com.me.tracking_order.review.service;

import com.me.tracking_order.review.dto.response.ProductVariantReviewResponse;
import com.me.tracking_order.review.dto.response.ReviewResponse;
import com.me.tracking_order.review.entity.Review;
import com.me.tracking_order.review.mapper.ReviewMapper;
import com.me.tracking_order.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public ProductVariantReviewResponse getProductVariantReviews(String id){
        List<Review> reviews = reviewRepository.findAllByProductVariantId(id);

        int totalReviews  = reviews.size();

        float averageRating = (float) reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);

        List<ReviewResponse> reviewResponses = reviews.stream().map(reviewMapper::toResponse).toList();

        return new ProductVariantReviewResponse(averageRating, totalReviews, reviewResponses);
    }
}
