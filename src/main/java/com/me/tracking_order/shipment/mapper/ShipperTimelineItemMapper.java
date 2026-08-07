package com.me.tracking_order.shipment.mapper;

import com.me.tracking_order.shipment.dto.shipper.response.ShipperTimelineItemResponse;
import com.me.tracking_order.shipment.entity.TrackingLog;
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
public interface ShipperTimelineItemMapper {

    @Mapping(target = "status", source = "newStatus")

    @Mapping(target = "shipmentId", source = "shipment.id")

    @Mapping(target = "trackingNumber", source = "shipment.trackingNumber")

    ShipperTimelineItemResponse toResponse(TrackingLog trackingLog);
}
