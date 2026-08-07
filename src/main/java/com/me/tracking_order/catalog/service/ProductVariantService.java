package com.me.tracking_order.catalog.service;

import com.me.tracking_order.catalog.dto.customer.response.FeaturedProductVariantResponse;
import com.me.tracking_order.catalog.dto.customer.response.ProductVariantDetailsResponse;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.catalog.mapper.ProductVariantDetailsMapper;
import com.me.tracking_order.catalog.repository.ProductVariantRepository;
import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.review.repository.ReviewRepository;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final int FEATURED_PRODUCT_LIMIT = 12;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantDetailsMapper  productVariantDetailsMapper;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<FeaturedProductVariantResponse> getFeaturedProductVariants() {
        Pageable pageable = PageRequest.of(0, FEATURED_PRODUCT_LIMIT);

        List<FeaturedProductVariantResponse> result = productVariantRepository.getFeaturedProductVariants(ShipmentStatus.DELIVERED, pageable);

        return result;
    }

    @Transactional(readOnly = true)
    public ProductVariantDetailsResponse  getProductVariantDetails(String productVariantId) {
        ProductVariant pv = productVariantRepository.findActiveById(productVariantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

        ProductVariantDetailsResponse response = productVariantDetailsMapper.toResponse(pv);

        int reviewCount = reviewRepository.getReviewCountByProductVariantId(productVariantId);

        response.setReviewCount(reviewCount);

        return response;
    }
}
