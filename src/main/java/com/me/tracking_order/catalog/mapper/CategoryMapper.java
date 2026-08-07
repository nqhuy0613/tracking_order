package com.me.tracking_order.catalog.mapper;

import com.me.tracking_order.catalog.dto.customer.response.CategoryResponse;
import com.me.tracking_order.catalog.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);
}
