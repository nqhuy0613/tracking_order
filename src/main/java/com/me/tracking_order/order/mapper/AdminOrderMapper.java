package com.me.tracking_order.order.mapper;

import com.me.tracking_order.order.dto.admin.response.AdminOrderResponse;
import com.me.tracking_order.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AdminOrderMapper {
    @Mapping(target = "shipmentStatus", source = "shipment.status")
    @Mapping(
            target = "customerName",
            source = "shipment.receiverName"
    )

    @Mapping(
            target = "carrierName",
            source = "shipment.carrier.name"
    )
    AdminOrderResponse toResponse(Order order);
}
