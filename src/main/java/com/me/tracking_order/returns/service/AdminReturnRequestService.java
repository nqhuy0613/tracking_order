package com.me.tracking_order.returns.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.common.response.PageResponse;
import com.me.tracking_order.returns.dto.admin.response.AdminDetailsReturnResponse;
import com.me.tracking_order.returns.dto.admin.response.AdminReturnSummaryResponse;
import com.me.tracking_order.returns.dto.customer.response.ReturnRequestResponse;
import com.me.tracking_order.returns.entity.ReturnRequest;
import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import com.me.tracking_order.returns.mapper.AdminDetailReturnMapper;
import com.me.tracking_order.returns.mapper.ReturnRequestMapper;
import com.me.tracking_order.returns.repository.ReturnRequestRepository;
import com.me.tracking_order.returns.specification.ReturnRequestSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminReturnRequestService {

    private final int pageSize = 3;
    private final ReturnRequestRepository returnRequestRepository;
    private final AdminDetailReturnMapper adminDetailReturnMapper;
    private final ReturnRequestMapper returnRequestMapper;

    @Transactional(readOnly = true)
    public AdminDetailsReturnResponse getDetailsReturnById(String id){
        ReturnRequest returnRequest = returnRequestRepository.findActiveReturnById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RETURN_REQUEST_NOT_FOUND));

        return adminDetailReturnMapper.toResponse(returnRequest);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReturnRequestResponse> getAllReturnRequests(ReturnRequestStatus status, int pageNumber){
        Specification<ReturnRequest> specification = Specification.where(ReturnRequestSpecification.notDeleted());

        specification = specification.and(ReturnRequestSpecification.hasStatus(status));

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize,  Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ReturnRequest> returnRequestPage =
                returnRequestRepository.findAll(specification, pageable);

        return PageResponse.from(
                returnRequestPage,
                returnRequestMapper::toResponse
        );
    }

    @Transactional
    public ReturnRequestResponse markReturnRequestReceived(String id){
        ReturnRequest returnRequest = returnRequestRepository.findByIdAndIsDeletedFalseAndStatus(id, ReturnRequestStatus.IN_TRANSIT)
                .orElseThrow(() -> new BusinessException(ErrorCode.RETURN_REQUEST_NOT_VALID));

        returnRequest.setStatus(ReturnRequestStatus.RECEIVED);
        return returnRequestMapper.toResponse(returnRequest);
    }

    @Transactional(readOnly = true)
    public AdminReturnSummaryResponse getReturnRequestSummary(){
        long awaitingInspection = returnRequestRepository.countByIsDeletedFalseAndStatus(ReturnRequestStatus.RECEIVED);

        long activeReturns = returnRequestRepository.countByIsDeletedFalseAndStatusIn(
                List.of(ReturnRequestStatus.RECEIVED, ReturnRequestStatus.IN_TRANSIT, ReturnRequestStatus.PENDING));

        BigDecimal totalRefunds = returnRequestRepository.totalRefunds(ReturnRequestStatus.REFUNDED);

        return new AdminReturnSummaryResponse(activeReturns, awaitingInspection, totalRefunds);
    }
}
