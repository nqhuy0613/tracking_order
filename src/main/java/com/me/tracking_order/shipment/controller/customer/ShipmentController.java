package com.me.tracking_order.shipment.controller.customer;


import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.shipment.dto.customer.response.ShipmentDetailResponse;
import com.me.tracking_order.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentDetailResponse>> getShipmentDetail(
            @PathVariable String orderId,
            Authentication authentication
    ) {

        ShipmentDetailResponse result = shipmentService.getShipmentDetail(
                orderId,
                authentication.getName()
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Shipment detail retrieved successfully",
                result
        ));
    }

    @PutMapping("/mark-delivered/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentDetailResponse>> markShipmentDelivered(
            @PathVariable String orderId,
            Authentication authentication
    ) {
        ShipmentDetailResponse result = shipmentService.markShipmentDelivered(
                orderId,
                authentication.getName()
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Shipment marked as delivered successfully",
                result
        ));
    }
}
