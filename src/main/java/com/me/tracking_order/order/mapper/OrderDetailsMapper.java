package com.me.tracking_order.order.mapper;

import com.me.tracking_order.order.dto.customer.response.OrderDetailsResponse;
import com.me.tracking_order.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = OrderItemMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrderDetailsMapper {

    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "status", source = "shipment.status")
    @Mapping(
            target = "trackingNumber",
            source = "shipment.trackingNumber"
    )
    @Mapping(
            target = "estimatedDelivery",
            source = "shipment.estimatedDelivery"
    )
    @Mapping(
            target = "receiverName",
            source = "shipment.receiverName"
    )
    @Mapping(
            target = "receiverPhone",
            source = "shipment.receiverPhone"
    )
    @Mapping(
            target = "shippingProvince",
            source = "shipment.shippingProvince"
    )
    @Mapping(
            target = "shippingCommune",
            source = "shipment.shippingCommune"
    )
    @Mapping(
            target = "shippingDetailAddress",
            source = "shipment.shippingDetailAddress"
    )
    @Mapping(
            target = "currentLocation",
            source = "shipment.currentLocation"
    )
    @Mapping(
            target = "carrierName",
            source = "shipment.carrier.name"
    )
    OrderDetailsResponse toResponse(Order order);
}
