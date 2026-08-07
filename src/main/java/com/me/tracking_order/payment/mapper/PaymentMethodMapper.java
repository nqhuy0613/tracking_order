package com.me.tracking_order.payment.mapper;

import com.me.tracking_order.payment.dto.response.AdminPaymentMethodResponse;
import com.me.tracking_order.payment.entity.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PaymentMethodMapper {

    AdminPaymentMethodResponse toResponse(
            PaymentMethod paymentMethod
    );
}
