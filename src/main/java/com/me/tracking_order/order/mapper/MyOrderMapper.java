package com.me.tracking_order.order.mapper;

import com.me.tracking_order.order.dto.customer.response.OrderResponse;
import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.order.enums.MyOrderStatus;
import com.me.tracking_order.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MyOrderMapper {

    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "totalAmount", source = "order.totalAmount")
    @Mapping(target = "createdAt", source = "order.createdAt")
    @Mapping(target = "paymentMethod", source = "payment.paymentMethod.type")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "itemCount", source = "itemCount")
    OrderResponse toResponse(
            Order order,
            Payment payment,
            MyOrderStatus status,
            int itemCount
    );
}
