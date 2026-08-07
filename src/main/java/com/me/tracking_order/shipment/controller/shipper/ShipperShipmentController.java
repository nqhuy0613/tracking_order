package com.me.tracking_order.shipment.controller.shipper;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.shipment.dto.shipper.request.MarkFailedRequest;
import com.me.tracking_order.shipment.dto.shipper.response.*;
import com.me.tracking_order.shipment.service.ShipperShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipper/shipments")
@RequiredArgsConstructor
public class ShipperShipmentController {

    private final ShipperShipmentService shipperShipmentService;

    @PreAuthorize("hasRole('SHIPPER')")
    @GetMapping("/daily-summary")
    public ResponseEntity<ApiResponse<ShipperDailySummaryResponse>> getDailySummary(
            Authentication authentication
    ){
        ShipperDailySummaryResponse result = shipperShipmentService.getDailySummary(authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(
                "Shipper daily summary retrived successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('SHIPPER')")
    @GetMapping("/in-progress")
    public ResponseEntity<ApiResponse<InProgressOrderResponse>> getInProgressOrder(
            Authentication authentication
    ) {
        InProgressOrderResponse result = shipperShipmentService.getInProgressOrder(authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(
                "Order in-progress retrived successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('SHIPPER')")
    @PutMapping("/mark-delivered/{id}")
    public ResponseEntity<ApiResponse<ShipperUpdateStatusResponse>> markDelivered(
            Authentication authentication,
            @PathVariable("id")  String id
    ) {
        ShipperUpdateStatusResponse result = shipperShipmentService.markDelivered(id, authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(
                "Shipper mark delivered successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('SHIPPER')")
    @PutMapping("/mark-failed/{id}")
    public ResponseEntity<ApiResponse<ShipperUpdateStatusResponse>> markFailed(
            Authentication authentication,
            @PathVariable("id")  String id,
            @Valid @RequestBody MarkFailedRequest request
    ) {
        ShipperUpdateStatusResponse result = shipperShipmentService.markFailed(id, authentication.getName(), request);

        return ResponseEntity.ok(ApiResponse.success(
                "Shipper mark failed successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('SHIPPER')")
    @GetMapping("/queue")
    public ResponseEntity<ApiResponse<ShipperQueuePreviewResponse>> getQueue(
            Authentication authentication
    ) {
        ShipperQueuePreviewResponse result = shipperShipmentService.getQueue(authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(
                "Shipper queue retrived successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('SHIPPER')")
    @GetMapping("/today-timeline")
    public ResponseEntity<ApiResponse<List<ShipperTimelineItemResponse>>> getTodayTimeline(
            Authentication authentication
    ) {
        List<ShipperTimelineItemResponse> result = shipperShipmentService.getTodayTimeline(authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(
                "Today timeline retrived successfully",
                result
        ));
    }
}
