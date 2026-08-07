package com.me.tracking_order.order.service;

import com.me.tracking_order.catalog.entity.Inventory;
import com.me.tracking_order.catalog.entity.ProductVariant;
import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.common.response.PageResponse;
import com.me.tracking_order.order.dto.admin.request.AdminOrderFilterRequest;
import com.me.tracking_order.order.dto.admin.request.BulkConfirmOrderRequest;
import com.me.tracking_order.order.dto.admin.request.RejectOrderRequest;
import com.me.tracking_order.order.dto.admin.response.*;
import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.order.entity.OrderItem;
import com.me.tracking_order.order.mapper.AdminOrderMapper;
import com.me.tracking_order.order.repository.OrderItemRepository;
import com.me.tracking_order.order.repository.OrderRepository;
import com.me.tracking_order.order.specification.OrderSpecification;
import com.me.tracking_order.payment.entity.Payment;
import com.me.tracking_order.payment.repository.PaymentRepository;
import com.me.tracking_order.shipment.entity.Shipment;
import com.me.tracking_order.shipment.entity.TrackingLog;
import com.me.tracking_order.shipment.enums.ShipmentStatus;
import com.me.tracking_order.shipment.repository.ShipmentRepository;
import com.me.tracking_order.shipment.repository.TrackingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderService {


    private final AdminOrderMapper adminOrderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;
    private final TrackingLogRepository trackingLogRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminOrderResponse> getAllOrders(AdminOrderFilterRequest orderFilterRequest, Integer pageNumber, Integer pageSize){
        Specification specification = Specification.where(OrderSpecification.notDeleted());

        if (orderFilterRequest != null) {
            if (orderFilterRequest.getMinAmount() != null
                    && orderFilterRequest.getMaxAmount() != null
                    && orderFilterRequest.getMinAmount().compareTo(orderFilterRequest.getMaxAmount()) > 0) {
                throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
            }

            if (orderFilterRequest.getMinAmount() != null) {
                specification = specification.and(OrderSpecification.minAmount(orderFilterRequest.getMinAmount()));
            }

            if (orderFilterRequest.getMaxAmount() != null) {
                specification = specification.and(OrderSpecification.maxAmount(orderFilterRequest.getMaxAmount()));
            }

            if (orderFilterRequest.getShipmentStatus() != null) {
                specification = specification.and(OrderSpecification.hasShipmentStatus(orderFilterRequest.getShipmentStatus()));
            }

            if (orderFilterRequest.getCarrierName() != null && !orderFilterRequest.getCarrierName().isEmpty()) {
                specification = specification.and(OrderSpecification.hasCarrierName(orderFilterRequest.getCarrierName()));
            }

            if (orderFilterRequest.getYear() != null) {
                specification = specification.and(OrderSpecification.createdinYear(orderFilterRequest.getYear()));
            }

        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> page = orderRepository.findAll(specification, pageable);

        return PageResponse.from(
                page,
                adminOrderMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public AdminOrdersSummary getOrdersSummary(){

        // todo: chuyen ve 1 cau sql

        return orderRepository.getAdminOrderSummary(
                ShipmentStatus.DELIVERED,
                ShipmentStatus.PENDING,
                ShipmentStatus.SHIPPING,
                ShipmentStatus.FAILED
        );
    }

    @Transactional(readOnly = true)
    public AdminOrdersDailySummary getOrdersDailySummary(){
        LocalDate today = LocalDate.now();

        LocalDateTime startTime =
                today.atStartOfDay();

        LocalDateTime endTime =
                today.plusDays(1).atStartOfDay();

        return orderRepository.getDailySummary(
                startTime,
                endTime,
                ShipmentStatus.PENDING,
                ShipmentStatus.PENDING
        );
    }

    @Transactional
    public BulkConfirmOrderResponse bulkConfirmOrders(BulkConfirmOrderRequest bulkConfirmOrderRequest){

        HashSet<String> orderIds = new HashSet<>(bulkConfirmOrderRequest.getOrderIds());
        if (orderIds.size() != bulkConfirmOrderRequest.getOrderIds().size() || orderIds.size() == 0) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_SELECTION);
        }

        List<Shipment> shipments = shipmentRepository.findByOrderIds(new ArrayList<>(orderIds), ShipmentStatus.PENDING);

        if (shipments.size() != orderIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_SELECTION);
        }

        List<TrackingLog>  trackingLogs = new ArrayList<>();

        for (Shipment shipment : shipments) {
            shipment.setStatus(ShipmentStatus.CONFIRMED);
            TrackingLog tl = new TrackingLog();
            tl.setTitle("Order confirmed");
            tl.setOldStatus(String.valueOf(ShipmentStatus.PENDING));
            tl.setNewStatus(String.valueOf(ShipmentStatus.CONFIRMED));
            tl.setDescription("Admin confirmed order");
            tl.setShipment(shipment);
            trackingLogs.add(tl);
        }
        // todo: chuyen thanh saveall
        trackingLogRepository.saveAll(trackingLogs);

        return BulkConfirmOrderResponse.builder()
                .confirmedCount(shipments.size())
                .build();
    }

    @Transactional
    public RejectOrderResponse rejectOrder(RejectOrderRequest rejectOrderRequest, String orderId){
        Shipment shipment = shipmentRepository
                .findActiveByOrderIdAndStatus(
                        orderId,
                        ShipmentStatus.PENDING
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.INVALID_ORDER_SELECTION
                        )
                );

        shipment.setStatus(ShipmentStatus.FAILED);

        TrackingLog trackingLog = new TrackingLog();
        trackingLog.setOldStatus(
                ShipmentStatus.PENDING.name()
        );
        trackingLog.setNewStatus(
                ShipmentStatus.FAILED.name()
        );
        trackingLog.setTitle("Order rejected");
        trackingLog.setDescription(
                rejectOrderRequest.getDescription()
        );
        trackingLog.setShipment(shipment);

        trackingLogRepository.save(trackingLog);

        return RejectOrderResponse.builder()
                .orderId(orderId)
                .status(ShipmentStatus.FAILED)
                .description(
                        rejectOrderRequest.getDescription()
                )
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminPendingOrderResponse> getPendingOrders(Boolean isLowStock, Integer pageNumber, Integer pageSize){
        Specification<Order> specification =
                Specification.where(
                                OrderSpecification.notDeleted()
                        )
                        .and(
                                OrderSpecification
                                        .hasActiveShipmentStatus(
                                                ShipmentStatus.PENDING
                                        )
                        )
                        .and(
                                OrderSpecification
                                        .hasInsufficientStock(
                                                isLowStock
                                        )
                        );

        Pageable pageable = PageRequest.of(
                pageNumber - 1,
                pageSize,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<Order> orderPage =
                orderRepository.findAll(
                        specification,
                        pageable
                );

        List<String> orderIds = orderPage
                .getContent()
                .stream()
                .map(Order::getId)
                .toList();

        List<OrderItem> orderItems =
                orderIds.isEmpty()
                        ? List.of()
                        : orderItemRepository
                                .findActiveWithVariantAndInventoryByOrderIds(
                                        orderIds
                                );

        List<Payment> payments =
                orderIds.isEmpty()
                        ? List.of()
                        : paymentRepository
                                .findActiveWithMethodByOrderIds(
                                        orderIds
                                );

        Map<String, List<OrderItem>> orderItemsByOrderId =
                orderItems.stream()
                        .collect(
                                Collectors.groupingBy(
                                        orderItem ->
                                                orderItem
                                                        .getOrder()
                                                        .getId()
                                )
                        );

        Map<String, Payment> latestPaymentByOrderId =
                new HashMap<>();

        for (Payment payment : payments) {
            latestPaymentByOrderId.putIfAbsent(
                    payment.getOrder().getId(),
                    payment
            );
        }

        return PageResponse.from(
                orderPage,
                order -> toPendingOrderResponse(
                        order,
                        orderItemsByOrderId.getOrDefault(
                                order.getId(),
                                List.of()
                        ),
                        latestPaymentByOrderId.get(
                                order.getId()
                        )
                )
        );
    }

    private AdminPendingOrderResponse toPendingOrderResponse(
            Order order,
            List<OrderItem> orderItems,
            Payment payment
    ) {
        Shipment shipment = order.getShipment();

        List<ProductPendingOrderResponse> products =
                orderItems.stream()
                        .map(this::toProductPendingOrderResponse)
                        .toList();

        AdminPendingOrderResponse response =
                new AdminPendingOrderResponse();

        response.setOrderId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentStatus(
                order.getPaymentStatus()
        );
        response.setElapsedMinutes(
                Duration.between(
                        order.getCreatedAt(),
                        LocalDateTime.now()
                ).toMinutes()
        );
        response.setReceiverName(
                shipment.getReceiverName()
        );
        response.setAddress(
                String.join(
                        ", ",
                        shipment.getShippingDetailAddress(),
                        shipment.getShippingCommune(),
                        shipment.getShippingProvince()
                )
        );
        response.setPaymentMethod(
                payment == null
                        ? null
                        : payment
                                .getPaymentMethod()
                                .getName()
        );
        response.setLowStock(
                orderItems.stream()
                        .anyMatch(this::hasInsufficientStock)
        );
        response.setProductPendingOrders(products);

        return response;
    }

    private ProductPendingOrderResponse
    toProductPendingOrderResponse(
            OrderItem orderItem
    ) {
        ProductVariant productVariant =
                orderItem.getProductVariant();
        Inventory inventory =
                productVariant.getInventory();

        ProductPendingOrderResponse response =
                new ProductPendingOrderResponse();

        response.setUnitPrice(orderItem.getUnitPrice());
        response.setName(productVariant.getName());
        response.setQuantity(orderItem.getQuantity());
        response.setQuantityInStock(
                inventory == null || inventory.isDeleted()
                        ? 0
                        : inventory.getQuantityInStock()
        );

        return response;
    }

    private boolean hasInsufficientStock(
            OrderItem orderItem
    ) {
        ProductVariant productVariant =
                orderItem.getProductVariant();
        Inventory inventory =
                productVariant.getInventory();

        return productVariant.isDeleted()
                || inventory == null
                || inventory.isDeleted()
                || inventory.getQuantityInStock()
                        < orderItem.getQuantity();
    }
}
