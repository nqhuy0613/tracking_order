package com.me.tracking_order.payment.controller.admin;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.payment.dto.request.UpdatePaymentMethodStatusRequest;
import com.me.tracking_order.payment.dto.response.AdminPaymentMethodResponse;
import com.me.tracking_order.payment.service.AdminPaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/payment-methods")
@RequiredArgsConstructor
public class AdminPaymentMethodController {

    private final AdminPaymentMethodService adminPaymentMethodService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminPaymentMethodResponse>>> getAllPaymentMethods() {
        List<AdminPaymentMethodResponse> result =
                adminPaymentMethodService.getAllPaymentMethods();

        return ResponseEntity.ok(ApiResponse.success(
                "Payment methods retrieved successfully",
                result
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("status/{id}")
    public ResponseEntity<ApiResponse<AdminPaymentMethodResponse>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdatePaymentMethodStatusRequest request
    ) {
        AdminPaymentMethodResponse result =
                adminPaymentMethodService.updateStatus(id, request);

        return ResponseEntity.ok(ApiResponse.success(
                "Payment method status updated successfully",
                result
        ));
    }
}
