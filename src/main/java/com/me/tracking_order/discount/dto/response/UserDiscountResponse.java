package com.me.tracking_order.discount.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserDiscountResponse {

    private String userDiscountId;
    private String discountId;
    private String code;
    private String description;
    private BigDecimal discountPercentage;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private LocalDateTime startAt;
    private LocalDateTime expiredAt;
}
