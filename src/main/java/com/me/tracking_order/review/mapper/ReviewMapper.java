package com.me.tracking_order.review.mapper;


import com.me.tracking_order.review.dto.response.ReviewResponse;
import com.me.tracking_order.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReviewMapper {
    ReviewResponse toResponse(Review review);
}

