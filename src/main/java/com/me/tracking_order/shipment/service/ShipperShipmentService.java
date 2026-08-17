package com.me.tracking_order.shipment.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.order.repository.OrderItemRepository;
import com.me.tracking_order.security.CurrentUserProvider;
import com.me.tracking_order.shipment.dto.shipper.request.MarkFailedRequest;
import com.me.tracking_order.shipment.dto.shipper.response.*;
import com.me.tracking_order.shipment.entity.Shipment;
import com.me.tracking_order.shipment.entity.ShipmentAssignment;
import com.me.tracking_order.shipment.entity.TrackingLog;
import com.me.tracking_order.shipment.enums.AssignmentStatus;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import com.me.tracking_order.shipment.mapper.InProgressOrderMapper;
import com.me.tracking_order.shipment.mapper.ShipperQueueItemMapper;
import com.me.tracking_order.shipment.mapper.ShipperTimelineItemMapper;
import com.me.tracking_order.shipment.repository.ShipmentAssignmentRepository;
import com.me.tracking_order.shipment.repository.TrackingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipperShipmentService {

    private final ShipmentAssignmentRepository shipmentAssignmentRepository;
    private final OrderItemRepository orderItemRepository;
    private final InProgressOrderMapper  inProgressOrderMapper;
    private final TrackingLogRepository trackingLogRepository;
    private final ShipperQueueItemMapper shipperQueueItemMapper;
    private final ShipperTimelineItemMapper shipperTimelineItemMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public ShipperDailySummaryResponse getDailySummary(){
        String username = currentUserProvider.getRequiredUsername();

        LocalDate today = LocalDate.now();

        LocalDateTime startAt = today.atStartOfDay();
        LocalDateTime endAt = today.plusDays(1).atStartOfDay();

        ShipperDailySummaryResponse response = shipmentAssignmentRepository.getShipperDailySummary(
                username,
                AssignmentStatus.COMPLETED,
                AssignmentStatus.FAILED,
                startAt,
                endAt
        );

        return response;
    }

    @Transactional(readOnly = true)
    public InProgressOrderResponse getInProgressOrder(){
        String username = currentUserProvider.getRequiredUsername();

        ShipmentAssignment shipmentAssignment = shipmentAssignmentRepository.getActiveShipmentAssignmentByUsernameAndStatus(
                username,
                AssignmentStatus.IN_PROGRESS
        ).orElseThrow(() -> new BusinessException(ErrorCode.IN_PROGRESS_ORDER_NOT_FOUND));

        int orderItemCount = orderItemRepository.countByIsDeletedFalseAndOrder_Id(shipmentAssignment.getShipment().getOrder().getId());

        return inProgressOrderMapper.toResponse(shipmentAssignment, orderItemCount);
    }

    @Transactional
    public ShipperUpdateStatusResponse markDelivered(String id){
        String username = currentUserProvider.getRequiredUsername();

        ShipmentAssignment shipmentAssignment = shipmentAssignmentRepository.getActiveShipmentAssignmentByUsernameAndStatusAndId(
                username,
                AssignmentStatus.IN_PROGRESS,
                id
        ).orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_ASSIGNMENT_NOT_VALID));


        Shipment shipment =  shipmentAssignment.getShipment();
        if (shipment.getStatus() != ShipmentStatus.SHIPPING) {
            throw new BusinessException(
                    ErrorCode.SHIPMENT_ASSIGNMENT_NOT_VALID
            );
        }


        shipmentAssignment.setStatus(AssignmentStatus.COMPLETED);
        shipmentAssignment.setFinishedAt(LocalDateTime.now());

        shipment.setStatus(ShipmentStatus.DELIVERED);
        TrackingLog trackingLog = new TrackingLog();
        trackingLog.setNewStatus(String.valueOf(ShipmentStatus.DELIVERED));
        trackingLog.setShipment(shipment);
        trackingLog.setOldStatus(String.valueOf(ShipmentStatus.SHIPPING));
        trackingLog.setTitle("Delivered Shipment");
        trackingLogRepository.save(trackingLog);

        return new ShipperUpdateStatusResponse(id, AssignmentStatus.COMPLETED, "Delivered Shipment");
    }

    @Transactional
    public ShipperUpdateStatusResponse markFailed(String id, MarkFailedRequest request){
        String username = currentUserProvider.getRequiredUsername();

        ShipmentAssignment shipmentAssignment = shipmentAssignmentRepository.getActiveShipmentAssignmentByUsernameAndStatusAndId(
                username,
                AssignmentStatus.IN_PROGRESS,
                id
        ).orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_ASSIGNMENT_NOT_VALID));


        Shipment shipment =  shipmentAssignment.getShipment();
        if (shipment.getStatus() != ShipmentStatus.SHIPPING) {
            throw new BusinessException(
                    ErrorCode.SHIPMENT_ASSIGNMENT_NOT_VALID
            );
        }

        String reason = request.getReason().trim();
        shipmentAssignment.setStatus(AssignmentStatus.FAILED);
        shipmentAssignment.setFinishedAt(LocalDateTime.now());
        shipmentAssignment.setFailureReason(reason);

        shipment.setStatus(ShipmentStatus.FAILED);
        TrackingLog trackingLog = new TrackingLog();
        trackingLog.setNewStatus(String.valueOf(ShipmentStatus.FAILED));
        trackingLog.setShipment(shipment);
        trackingLog.setOldStatus(String.valueOf(ShipmentStatus.SHIPPING));
        trackingLog.setTitle("Failed Shipment");
        trackingLog.setDescription(reason);
        trackingLogRepository.save(trackingLog);

        return new ShipperUpdateStatusResponse(id, AssignmentStatus.COMPLETED, reason);
    }

    @Transactional(readOnly = true)
    public ShipperQueuePreviewResponse getQueue(){
        String username = currentUserProvider.getRequiredUsername();

        List<ShipmentAssignment> shipmentAssignments = shipmentAssignmentRepository.getAllActiveShipmentAssignmentByUsernameAndStatus(
                username,
                AssignmentStatus.ASSIGNED
        );

        int pendingCount = shipmentAssignments.size();

        // tạo map id, số orderItem
        Map<String, Integer> orderItemCountMap = shipmentAssignments.stream()
                .collect(Collectors.toMap(assignMent ->
                        assignMent.getShipment()
                                .getOrder()
                                .getId(),
                        assignMent -> Math.toIntExact(
                                assignMent.getShipment()
                                        .getOrder()
                                        .getOrderItems()
                                        .stream()
                                        .filter(x -> !x.isDeleted())
                                        .count()
                        )
                ));

        List<ShipperQueueItemResponse> responses =
                shipmentAssignments.stream()
                        .limit(2)
                        .map(assignment -> {
                            String orderId = assignment
                                    .getShipment()
                                    .getOrder()
                                    .getId();

                            int orderItemCount =
                                    orderItemCountMap.getOrDefault(orderId, 0);

                            return shipperQueueItemMapper.toResponse(
                                    assignment,
                                    orderItemCount
                            );
                        })
                        .toList();

        return new ShipperQueuePreviewResponse(
                pendingCount,
                responses
        );

    }

    @Transactional(readOnly = true)
    public List<ShipperTimelineItemResponse> getTodayTimeline(){
        String username = currentUserProvider.getRequiredUsername();

        LocalDate today =  LocalDate.now();

        LocalDateTime startAt = today.atStartOfDay();
        LocalDateTime endAt = startAt.plusDays(1);

        List<TrackingLog> trackingLogs = trackingLogRepository.findActiveShipperOwnedTrackingLog(username,startAt,endAt);

        return trackingLogs.stream()
                .map(shipperTimelineItemMapper::toResponse)
                .toList();
    }
}
