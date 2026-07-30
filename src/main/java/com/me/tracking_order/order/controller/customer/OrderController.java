package com.me.tracking_order.order.controller.customer;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.order.dto.customer.request.CreateOrderRequest;
import com.me.tracking_order.order.dto.customer.request.OrderSummaryRequest;
import com.me.tracking_order.order.dto.customer.response.CreateOrderResponse;
import com.me.tracking_order.order.dto.customer.response.OrderResponse;
import com.me.tracking_order.order.dto.customer.response.OrderSummaryResponse;
import com.me.tracking_order.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<OrderSummaryResponse>> getOrderSummary(
            Authentication authentication,
            @Valid @RequestBody OrderSummaryRequest orderSummaryRequest
    ) {

        OrderSummaryResponse result =
                orderService.getOrderSummary(

                        authentication.getName(),
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
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest createOrderRequest
    ){
        CreateOrderResponse result = orderService.createOrder(

                createOrderRequest,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Order created successfully",
                        result
                ));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(
            Authentication authentication,
            @PathVariable("orderId") String orderId){
        OrderResponse result = orderService.getOrderDetails(
                authentication.getName(),
                orderId
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Order details retrieved successfully",
                result
        ));
    }
}
