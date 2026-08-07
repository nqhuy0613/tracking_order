package com.me.tracking_order.shipment.mapper;

import com.me.tracking_order.shipment.dto.admin.response.AdminCarrierResponse;
import com.me.tracking_order.shipment.entity.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        // map struct sinh ra class implementation
        componentModel = MappingConstants.ComponentModel.SPRING,
        // bao loi neu object chua duoc mapping
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CarrierMapper {
    AdminCarrierResponse toResponse(Carrier carrier);
}
