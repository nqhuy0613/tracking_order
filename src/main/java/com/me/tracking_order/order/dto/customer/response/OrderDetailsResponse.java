package com.me.tracking_order.order.dto.customer.response;

import com.me.tracking_order.payment.enums.PaymentStatus;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDetailsResponse {

    private String id;

    private BigDecimal totalAmount;

    private PaymentStatus paymentStatus;

    private List<OrderItemResponse> orderItems;

    private ShipmentStatus status;

    private String trackingNumber;

    private LocalDateTime estimatedDelivery;

    private String receiverName;

    private String receiverPhone;

    private String shippingProvince;

    private String shippingCommune;

    private String shippingDetailAddress;

    private String currentLocation;

    private String carrierName;

    private LocalDateTime createdAt;
}
