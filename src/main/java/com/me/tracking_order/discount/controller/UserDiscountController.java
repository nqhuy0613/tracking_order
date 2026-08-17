package com.me.tracking_order.discount.controller;

import com.me.tracking_order.common.response.ApiResponse;
import com.me.tracking_order.discount.dto.response.UserDiscountResponse;
import com.me.tracking_order.discount.service.UserDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class UserDiscountController {

    private final UserDiscountService userDiscountService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDiscountResponse>>>
            getCurrentUserDiscounts() {
        List<UserDiscountResponse> discounts = userDiscountService
                .getCurrentUserDiscounts();

        ApiResponse<List<UserDiscountResponse>> response =
                ApiResponse.success(
                        "User discounts retrieved successfully",
                        discounts
                );

        return ResponseEntity.ok(response);
    }
}
