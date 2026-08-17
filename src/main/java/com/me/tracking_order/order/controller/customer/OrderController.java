package com.me.tracking_order.order.controller.customer;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.order.dto.customer.request.CreateOrderRequest;
import com.me.tracking_order.order.dto.customer.request.CreateReviewRequest;
import com.me.tracking_order.order.dto.customer.request.OrderSummaryRequest;
import com.me.tracking_order.order.dto.customer.response.*;
import com.me.tracking_order.order.enums.MyOrderStatus;
import com.me.tracking_order.order.service.OrderService;
import com.me.tracking_order.review.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<OrderSummaryResponse>> getOrderSummary(
            @Valid @RequestBody OrderSummaryRequest orderSummaryRequest
    ) {

        OrderSummaryResponse result =
                orderService.getOrderSummary(

                        orderSummaryRequest
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order summary calculated successfully",
                        result
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>>  createOrder(
            @Valid @RequestBody CreateOrderRequest createOrderRequest
    ){
        CreateOrderResponse result = orderService.createOrder(

                createOrderRequest
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Order created successfully",
                        result
                ));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailsResponse>> getOrderDetails(
            @PathVariable("orderId") String orderId){
        OrderDetailsResponse result = orderService.getOrderDetails(
                orderId
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Order details retrieved successfully",
                result
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @RequestParam(required = false) MyOrderStatus status
    ) {
        List<OrderResponse> result = orderService.getMyOrders(
                status
                );

        return ResponseEntity.ok(ApiResponse.success(
                "My orders retrieved successfully",
                result
        ));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<OrderStatistics>> getOrderStatistics()
    {
        OrderStatistics result = orderService.getOrderStatistics();

        return ResponseEntity.ok(ApiResponse.success(
                "Order statistics retrieved successfully",
                result
        ));
    }

    @PostMapping("/reorder/{id}")
    public ResponseEntity<ApiResponse<List<ReorderItemResponse>>> reorder(
            @PathVariable("id") String id
    ) {
        List<ReorderItemResponse> result = orderService.reorder(id);

        return ResponseEntity.ok(ApiResponse.success(
                "Order reordered successfully",
                result
        ));
    }

    @PostMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> review(
            @PathVariable("id") String id,
            @Valid @RequestBody CreateReviewRequest request
            ) {
        List<ReviewResponse> result = orderService.review(id, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Create reviews successfully",
                result
        ));
    }

}
