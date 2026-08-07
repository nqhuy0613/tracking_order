package com.me.tracking_order.auth.dto.request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email is invalid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Phone must not be blank")
    @Pattern(
            regexp = "^0[35789]\\d{8}$",
            message = "Phone number is invalid"
    )
    private String phone;

    @NotNull(message = "Date of birth must not be null")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotNull(message = "Gender must not be null")
    private String gender;

    @NotBlank(message = "Password must not be blank")
    @Size(
            min = 8,
            max = 64,
            message = "Password must contain between 8 and 64 characters"
    )
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Password must contain at least one letter and one number"
    )
    private String password;

    @NotBlank(message = "Username must not be blank")
    @Size(
            min = 4,
            max = 36,
            message = "Username must contain between 4 and 50 characters"
    )
    @Pattern(
            regexp = "^[A-Za-z0-9._-]+$",
            message = "Username may only contain letters, numbers, dots, underscores and hyphens"
    )
    private String username;

    @NotBlank(message = "Confirm password must not be blank")
    private String confirmPassword;
}
