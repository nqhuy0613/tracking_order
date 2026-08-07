package com.me.tracking_order.shipment.service;

import com.me.tracking_order.common.exception.BusinessException;
import com.me.tracking_order.common.exception.ErrorCode;
import com.me.tracking_order.shipment.dto.admin.request.CreateCarrierRequest;
import com.me.tracking_order.shipment.dto.admin.request.UpdateCarrierStatusRequest;
import com.me.tracking_order.shipment.dto.admin.response.AdminCarrierResponse;
import com.me.tracking_order.shipment.entity.Carrier;
import com.me.tracking_order.shipment.mapper.CarrierMapper;
import com.me.tracking_order.shipment.repository.CarrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    @Transactional(readOnly = true)
    public List<AdminCarrierResponse> getAllCarriers(){
        return carrierRepository.findAllByIsDeletedIsFalseOrderByNameAsc()
                .stream()
                .map(carrierMapper::toResponse)
                .toList();
    }

    @Transactional
    public AdminCarrierResponse updateStatus(String carrierId, UpdateCarrierStatusRequest request){
        Carrier carrier = carrierRepository.findByIdAndIsDeletedIsFalse(carrierId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARRIER_NOT_FOUND));

        carrier.setEnabled(request.getStatus());

        Carrier updatedCarrier = carrierRepository.save(carrier);

        return carrierMapper.toResponse(updatedCarrier);
    }

    @Transactional
    public AdminCarrierResponse createCarrier(CreateCarrierRequest request){
        String normalizedName = request.getName().trim();

        if (carrierRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new BusinessException(ErrorCode.CARRIER_ALREADY_EXISTS);
        }

        Carrier carrier = new Carrier();
        carrier.setName(normalizedName);
        carrier.setDescription(request.getDescription());
        carrier.setEnabled(request.getIsEnabled());

        Carrier savedCarrier = carrierRepository.save(carrier);

        return carrierMapper.toResponse(savedCarrier);
    }
}
