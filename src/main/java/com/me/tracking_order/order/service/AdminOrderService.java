package com.me.tracking_order.order.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.common.response.PageResponse;
import com.me.tracking_order.order.dto.admin.request.AdminOrderFilterRequest;
import com.me.tracking_order.order.dto.admin.response.AdminOrderResponse;
import com.me.tracking_order.order.entity.Order;
import com.me.tracking_order.order.mapper.AdminOrderMapper;
import com.me.tracking_order.order.repository.OrderRepository;
import com.me.tracking_order.order.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final Integer PAGE_SIZE = 3;

    private final AdminOrderMapper adminOrderMapper;
    private final OrderRepository orderRepository;

    public PageResponse<AdminOrderResponse> getAllOrders(AdminOrderFilterRequest orderFilterRequest, Integer pageNumber){
        Specification specification = Specification.where(OrderSpecification.notDeleted());

        if(orderFilterRequest != null){
            if(orderFilterRequest.getMinAmount() != null
                    && orderFilterRequest.getMaxAmount()!= null
                    && orderFilterRequest.getMinAmount().compareTo(orderFilterRequest.getMaxAmount()) > 0){
                throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
            }

            if(orderFilterRequest.getMinAmount() != null){
                specification = specification.and(OrderSpecification.minAmount(orderFilterRequest.getMinAmount()));
            }

            if(orderFilterRequest.getMaxAmount() != null){
                specification = specification.and(OrderSpecification.maxAmount(orderFilterRequest.getMaxAmount()));
            }

            if(orderFilterRequest.getShipmentStatus() != null){
                specification = specification.and(OrderSpecification.hasShipmentStatus(orderFilterRequest.getShipmentStatus()));
            }

            if(orderFilterRequest.getCarrierName() != null && !orderFilterRequest.getCarrierName().isEmpty()){
                specification = specification.and(OrderSpecification.hasCarrierName(orderFilterRequest.getCarrierName()));
            }

            if(orderFilterRequest.getYear() != null){
                specification = specification.and(OrderSpecification.createdinYear(orderFilterRequest.getYear()));
            }

        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> page = orderRepository.findAll(specification, pageable);

        return PageResponse.from(
                page,
                adminOrderMapper::toResponse
        );
    }
}
