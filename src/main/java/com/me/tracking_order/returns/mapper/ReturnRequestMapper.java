package com.me.tracking_order.returns.mapper;

import com.me.tracking_order.returns.dto.admin.response.AdminDetailsReturnResponse;
import com.me.tracking_order.returns.dto.customer.response.ReturnRequestResponse;
import com.me.tracking_order.returns.entity.ReturnRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReturnRequestMapper {
    @Mapping(
            target = "orderId",
            source = "order.id"
    )

    @Mapping(
            target = "customerName",
            source = "order.user.name"
    )
    ReturnRequestResponse toResponse(ReturnRequest returnRequest);

}
