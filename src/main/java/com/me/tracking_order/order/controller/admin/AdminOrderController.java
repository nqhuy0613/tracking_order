package com.me.tracking_order.order.controller.admin;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.common.response.PageResponse;
import com.me.tracking_order.order.dto.admin.request.AdminOrderFilterRequest;
import com.me.tracking_order.order.dto.admin.response.AdminOrderResponse;
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
            @Min(1) @RequestParam(defaultValue = "1") Integer pageNumber
            ) {
        PageResponse<AdminOrderResponse> result = adminOrderService.getAllOrders(request, pageNumber);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order retrieved successfully",
                        result
        ));
    }
}
