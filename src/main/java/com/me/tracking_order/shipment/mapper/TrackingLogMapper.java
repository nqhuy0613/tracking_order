package com.me.tracking_order.shipment.mapper;

import com.me.tracking_order.shipment.dto.customer.response.TrackingLogResponse;
import com.me.tracking_order.shipment.entity.TrackingLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        // map struct sinh ra class implementation
        componentModel = MappingConstants.ComponentModel.SPRING,
        // bao loi neu object chua duoc mapping
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TrackingLogMapper {
    TrackingLogResponse toResponse(TrackingLog trackingLog);
}
