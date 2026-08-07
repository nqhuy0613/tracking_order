package com.me.tracking_order.payment.dto.response;

import com.me.tracking_order.payment.enums.PaymentMethodStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdminPaymentMethodResponse {

    private String id;

    private PaymentMethodStatus type;

    private String name;

    private String paymentProvider;

    private String description;

    private BigDecimal feeAmount;

    private boolean isEnabled;
}
