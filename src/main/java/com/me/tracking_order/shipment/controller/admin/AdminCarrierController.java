package com.me.tracking_order.shipment.controller.admin;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.shipment.dto.admin.request.CreateCarrierRequest;
import com.me.tracking_order.shipment.dto.admin.request.UpdateCarrierStatusRequest;
import com.me.tracking_order.shipment.dto.admin.response.AdminCarrierResponse;
import com.me.tracking_order.shipment.service.AdminCarrierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/carriers")
@RequiredArgsConstructor
public class AdminCarrierController {

    private final AdminCarrierService adminCarrierService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminCarrierResponse>>> getAllCarriers() {
        List<AdminCarrierResponse> result = adminCarrierService.getAllCarriers();

        return ResponseEntity.ok(ApiResponse.success(
                "Carriers retrieved successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("status/{id}")
    public ResponseEntity<ApiResponse<AdminCarrierResponse>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateCarrierStatusRequest request) {
        AdminCarrierResponse result = adminCarrierService.updateStatus(id, request);

        return ResponseEntity.ok(ApiResponse.success(
                "Carrier status updated successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<AdminCarrierResponse>> createCarrier(
            @Valid @RequestBody CreateCarrierRequest request
    ) {
        AdminCarrierResponse result = adminCarrierService.createCarrier(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Carrier created successfully",
                        result
                ));
    }
}
