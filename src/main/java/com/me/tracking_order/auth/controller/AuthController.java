package com.me.tracking_order.auth.controller;

import com.me.tracking_order.auth.dto.request.RegisterRequest;
import com.me.tracking_order.auth.dto.response.UserResponse;
import com.me.tracking_order.auth.service.AuthService;
import com.me.tracking_order.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
        @Valid @RequestBody RegisterRequest registerRequest
    ) {
        UserResponse result = authService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "register successfully",
                result
        ));
    }
}
