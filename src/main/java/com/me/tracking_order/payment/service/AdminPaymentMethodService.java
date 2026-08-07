package com.me.tracking_order.payment.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.payment.dto.request.UpdatePaymentMethodStatusRequest;
import com.me.tracking_order.payment.dto.response.AdminPaymentMethodResponse;
import com.me.tracking_order.payment.entity.PaymentMethod;
import com.me.tracking_order.payment.mapper.PaymentMethodMapper;
import com.me.tracking_order.payment.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    @Transactional(readOnly = true)
    public List<AdminPaymentMethodResponse> getAllPaymentMethods() {
        return paymentMethodRepository.findAllByIsDeletedIsFalseOrderByNameAsc()
                .stream()
                .map(paymentMethodMapper::toResponse)
                .toList();
    }

    @Transactional
    public AdminPaymentMethodResponse updateStatus(
            String paymentMethodId,
            UpdatePaymentMethodStatusRequest request
    ) {
        PaymentMethod paymentMethod = paymentMethodRepository
                .findByIdAndIsDeletedIsFalse(paymentMethodId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PAYMENT_METHOD_NOT_FOUND
                ));

        paymentMethod.setEnabled(request.getStatus());

        PaymentMethod updatedPaymentMethod =
                paymentMethodRepository.save(paymentMethod);

        return paymentMethodMapper.toResponse(updatedPaymentMethod);
    }
}
