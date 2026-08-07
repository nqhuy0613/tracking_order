package com.me.tracking_order.shipment.mapper;

import com.me.tracking_order.shipment.dto.shipper.response.ShipperQueueItemResponse;
import com.me.tracking_order.shipment.entity.ShipmentAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        // map struct sinh ra class implementation
        componentModel = MappingConstants.ComponentModel.SPRING,
        // bao loi neu object chua duoc mapping
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ShipperQueueItemMapper {

    @Mapping(
            target = "trackingNumber",
            source = "shipmentAssignment.shipment.trackingNumber"
    )

    @Mapping(
            target = "orderItemCount",
            source = "orderItemCount"
    )

    ShipperQueueItemResponse toResponse(
            ShipmentAssignment shipmentAssignment,
            int orderItemCount);
}
