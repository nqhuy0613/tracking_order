package com.me.tracking_order.shipment.controller.customer;


import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.shipment.dto.customer.response.TrackingLogResponse;
import com.me.tracking_order.shipment.service.TrackingLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trackinglogs")
@RequiredArgsConstructor
public class TrackingLogController {

    private final TrackingLogService trackingLogService;

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<List<TrackingLogResponse>>> getTrackingLog(
            @PathVariable String orderId) {
        List<TrackingLogResponse> trackingLogResponse = trackingLogService.getTrackingLog(
                orderId
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Tracking logs retrived successfully",
                trackingLogResponse
        ));
    }
}
