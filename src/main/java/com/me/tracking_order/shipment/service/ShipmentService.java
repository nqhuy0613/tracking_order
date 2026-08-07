package com.me.tracking_order.shipment.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.shipment.dto.customer.response.ShipmentDetailResponse;
import com.me.tracking_order.shipment.entity.Shipment;
import com.me.tracking_order.shipment.entity.TrackingLog;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import com.me.tracking_order.shipment.mapper.ShipmentDetailMapper;
import com.me.tracking_order.shipment.repository.ShipmentRepository;
import com.me.tracking_order.shipment.repository.TrackingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentDetailMapper shipmentDetailMapper;
    private final TrackingLogRepository trackingLogRepository;

    @Transactional(readOnly = true)
    public ShipmentDetailResponse getShipmentDetail(String orderId, String userName){
        Shipment shipment = shipmentRepository.findActiveOwnedByOrderId(orderId,userName)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        return shipmentDetailMapper.toResponse(shipment);
    }

    @Transactional
    public ShipmentDetailResponse markShipmentDelivered(String orderId, String userName){
        Shipment shipment = shipmentRepository.findActiveOwnedByOrderIdAndStatus(orderId,userName, ShipmentStatus.SHIPPING)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        shipment.setStatus(ShipmentStatus.DELIVERED);

        TrackingLog trackingLog = new TrackingLog();
        trackingLog.setOldStatus(
                ShipmentStatus.SHIPPING.name()
        );
        trackingLog.setNewStatus(
                ShipmentStatus.DELIVERED.name()
        );
        trackingLog.setTitle("Order delivered");
        trackingLog.setShipment(shipment);

        trackingLogRepository.save(trackingLog);

        return shipmentDetailMapper.toResponse(shipment);
    }
}
