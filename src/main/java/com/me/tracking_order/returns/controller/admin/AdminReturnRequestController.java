package com.me.tracking_order.returns.controller.admin;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.common.response.PageResponse;
import com.me.tracking_order.returns.dto.admin.response.AdminDetailsReturnResponse;
import com.me.tracking_order.returns.dto.admin.response.AdminReturnSummaryResponse;
import com.me.tracking_order.returns.dto.customer.response.ReturnRequestResponse;
import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import com.me.tracking_order.returns.service.AdminReturnRequestService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
            @Min(1) @RequestParam(defaultValue = "1") int pageNumber,
            @Min(1) @RequestParam(defaultValue = "3") Integer pageSize
    ) {
        PageResponse<ReturnRequestResponse> result =
                adminReturnRequestService.getAllReturnRequests(
                        status,
                        pageNumber,
                        pageSize
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/export", produces = "text/csv")
    public void exportReturnRequests(
            @RequestParam(required = false) ReturnRequestStatus status,
            HttpServletResponse response
    ) throws IOException {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        String fileName = "return-requests-"+timeStamp+".csv";

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");

        // yeu cau tai file
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                        .filename(fileName, StandardCharsets.UTF_8)
                        .build()
                        .toString()
        );

        // khong cache file chua du lieu quan tri
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");

        adminReturnRequestService.exportReturnRequest(
                status,
                response.getOutputStream()
        );
    }
}
