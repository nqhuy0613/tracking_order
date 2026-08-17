package com.me.tracking_order.shipment.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.security.CurrentUserProvider;
import com.me.tracking_order.shipment.dto.customer.response.TrackingLogResponse;
import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.shipment.mapper.TrackingLogMapper;
import com.me.tracking_order.order.repository.OrderRepository;
import com.me.tracking_order.shipment.repository.TrackingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TrackingLogService {

    private final TrackingLogRepository trackingLogRepository;
    private final OrderRepository orderRepository;
    private final TrackingLogMapper trackingLogMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public List<TrackingLogResponse> getTrackingLog (String orderId) {
        String username = currentUserProvider.getRequiredUsername();

        Order order = orderRepository.findActiveOwnedOrder(
                username, orderId
        ).orElseThrow(
                () -> new BusinessException(ErrorCode.ORDER_NOT_FOUND)
        );

        return trackingLogRepository.findActiveOwnedTrackingLog(username, orderId)
                .stream()
                .map(trackingLogMapper::toResponse)
                .toList();
    }
}
