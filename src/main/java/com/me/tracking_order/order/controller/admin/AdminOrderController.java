package com.me.tracking_order.order.controller.admin;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.common.response.PageResponse;
import com.me.tracking_order.order.dto.admin.request.AdminOrderFilterRequest;
import com.me.tracking_order.order.dto.admin.request.BulkConfirmOrderRequest;
import com.me.tracking_order.order.dto.admin.request.RejectOrderRequest;
import com.me.tracking_order.order.dto.admin.response.*;
import com.me.tracking_order.order.service.AdminOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final AdminOrderService adminOrderService;

    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminOrderResponse>>> getAllOrders(
            @Valid @ModelAttribute AdminOrderFilterRequest request,
            @Min(1) @RequestParam(defaultValue = "1") Integer pageNumber,
            @Min(1) @RequestParam(defaultValue = "3") Integer pageSize
            ) {
        PageResponse<AdminOrderResponse> result = adminOrderService.getAllOrders(request, pageNumber, pageSize);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Orders retrieved successfully",
                        result
        ));
    }

    @PreAuthorize((("hasRole('ADMIN')")))
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AdminOrdersSummary>> getOrdersSummary(){
        AdminOrdersSummary result = adminOrderService.getOrdersSummary();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Orders summary retrieved successfully",
                        result
                ));
    }

    @PreAuthorize((("hasRole('ADMIN')")))
    @PutMapping("/bulk-confirm")
    public ResponseEntity<ApiResponse<BulkConfirmOrderResponse>> bulkConfirmOrders(
            @Valid @RequestBody BulkConfirmOrderRequest bulkConfirmOrderRequest
    ) {
        BulkConfirmOrderResponse result = adminOrderService.bulkConfirmOrders(bulkConfirmOrderRequest);

        return ResponseEntity.ok(ApiResponse.success(
                "Confirm orders successfully",
                result
        ));
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("/pending-orders")
    public ResponseEntity<ApiResponse<PageResponse<AdminPendingOrderResponse>>> getPendingOrders(
            @RequestParam(required = false) Boolean isLowStock,
            @Min(1) @RequestParam(defaultValue = "1") Integer pageNumber,
            @Min(1) @RequestParam(defaultValue = "3") Integer pageSize
    ){
        PageResponse<AdminPendingOrderResponse> result = adminOrderService.getPendingOrders(isLowStock, pageNumber, pageSize);

        return ResponseEntity.ok(ApiResponse.success(
                "Pending orders retrieved successfully",
                result
        ));
    }

    @PreAuthorize((("hasRole('ADMIN')")))
    @GetMapping("/daily-summary")
    public ResponseEntity<ApiResponse<AdminOrdersDailySummary>> getOrdersDailySummary(){
        AdminOrdersDailySummary result = adminOrderService.getOrdersDailySummary();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Orders daily summary retrieved successfully",
                        result
                ));
    }

    @PreAuthorize((("hasRole('ADMIN')")))
    @PutMapping("/reject/{orderId}")
    public ResponseEntity<ApiResponse<RejectOrderResponse>> rejectOrder(
            @PathVariable String orderId,
            @Valid @RequestBody RejectOrderRequest rejectOrderRequest
    ) {
        RejectOrderResponse result = adminOrderService.rejectOrder(rejectOrderRequest,orderId);

        return ResponseEntity.ok(ApiResponse.success(
                "Reject order successfully",
                result
        ));
    }
}
