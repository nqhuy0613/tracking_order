package com.me.tracking_order.shipment.mapper;

import com.me.tracking_order.shipment.dto.shipper.response.InProgressOrderResponse;
import com.me.tracking_order.shipment.entity.ShipmentAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface InProgressOrderMapper {

    @Mapping(target = "id", source = "assignment.id")
    @Mapping(
            target = "trackingNumber",
            source = "assignment.shipment.trackingNumber"
    )
    @Mapping(
            target = "receiverName",
            source = "assignment.shipment.receiverName"
    )
    @Mapping(
            target = "receiverPhone",
            source = "assignment.shipment.receiverPhone"
    )
    @Mapping(
            target = "address",
            expression = "java(buildAddress(assignment))"
    )
    @Mapping(target = "orderItemCounts", source = "itemQuantity")
    InProgressOrderResponse toResponse(
            ShipmentAssignment assignment,
            int itemQuantity
    );

    default String buildAddress(ShipmentAssignment assignment) {
        var shipment = assignment.getShipment();

        return String.join(
                ", ",
                shipment.getShippingDetailAddress(),
                shipment.getShippingCommune(),
                shipment.getShippingProvince()
        );
    }
}
