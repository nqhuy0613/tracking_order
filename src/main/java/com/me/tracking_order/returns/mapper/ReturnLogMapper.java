package com.me.tracking_order.returns.mapper;

import com.me.tracking_order.returns.dto.admin.response.ReturnLogResponse;
import com.me.tracking_order.returns.entity.ReturnLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReturnLogMapper {
    ReturnLogResponse toResponse(ReturnLog returnLog);
}
