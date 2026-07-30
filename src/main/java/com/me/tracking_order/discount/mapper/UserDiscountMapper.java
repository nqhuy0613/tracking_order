package com.me.tracking_order.discount.mapper;

import com.me.tracking_order.discount.dto.response.UserDiscountResponse;
import com.me.tracking_order.discount.entity.UserDiscount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserDiscountMapper {

    @Mapping(target = "userDiscountId", source = "id")
    @Mapping(target = "discountId", source = "discount.id")
    @Mapping(target = "code", source = "discount.code")
    @Mapping(target = "description", source = "discount.description")
    @Mapping(
            target = "discountPercentage",
            source = "discount.discountPercentage"
    )
    @Mapping(
            target = "maxDiscountAmount",
            source = "discount.maxDiscountAmount"
    )
    @Mapping(
            target = "minOrderAmount",
            source = "discount.minOrderAmount"
    )
    UserDiscountResponse toResponse(UserDiscount userDiscount);
}
