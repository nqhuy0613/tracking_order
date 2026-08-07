package com.me.tracking_order.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private String id;

    private String name;

    private String email;

    private String phone;

    private LocalDate dob;

    private String gender;

    private String username;

    private LocalDateTime createdAt;
}
