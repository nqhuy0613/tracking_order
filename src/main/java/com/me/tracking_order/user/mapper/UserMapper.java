package com.me.tracking_order.user.mapper;

import com.me.tracking_order.auth.dto.response.UserResponse;
import com.me.tracking_order.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    UserResponse toResponse(User user);
}
