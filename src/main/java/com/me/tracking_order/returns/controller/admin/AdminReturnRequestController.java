package com.me.tracking_order.returns.controller.admin;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.common.response.PageResponse;
import com.me.tracking_order.returns.dto.admin.response.AdminDetailsReturnResponse;
import com.me.tracking_order.returns.dto.admin.response.AdminReturnSummaryResponse;
import com.me.tracking_order.returns.dto.customer.response.ReturnRequestResponse;
import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import com.me.tracking_order.returns.service.AdminReturnRequestService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin/return-requests")
@RequiredArgsConstructor
public class AdminReturnRequestController {

    private final AdminReturnRequestService adminReturnRequestService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{returnRequestId}")
    public ResponseEntity<ApiResponse<AdminDetailsReturnResponse>> getDetailsReturnById(
            @PathVariable String returnRequestId) {
        AdminDetailsReturnResponse adminDetailsReturnResponse = adminReturnRequestService.getDetailsReturnById(returnRequestId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Return details retrieved successfully",
                        adminDetailsReturnResponse
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReturnRequestResponse>>> getReturnRequests(
            @RequestParam(required = false) ReturnRequestStatus status,
            @Min(1) @RequestParam(defaultValue = "1") int pageNumber
    ) {
        PageResponse<ReturnRequestResponse> result =
                adminReturnRequestService.getAllReturnRequests(
                        status,
                        pageNumber
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Return requests retrieved successfully",
                        result
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/mark-received/{id}")
    public ResponseEntity<ApiResponse<ReturnRequestResponse>> markReturnRequestReceived(
            @PathVariable String id
    ) {
        ReturnRequestResponse result = adminReturnRequestService.markReturnRequestReceived(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Return request updated successfully",
                        result
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AdminReturnSummaryResponse>> getReturnRequestSummary(){
        AdminReturnSummaryResponse result = adminReturnRequestService.getReturnRequestSummary();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Return request summary retrieved successfully",
                        result
                )
        );
    }
}
