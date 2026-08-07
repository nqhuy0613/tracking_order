package com.me.tracking_order.returns.mapper;

import com.me.tracking_order.returns.dto.admin.response.ReturnRequestCsvRow;
import com.me.tracking_order.returns.entity.ReturnRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReturnRequestCsvRowMapper {
    @Mapping(target = "returnId", source = "id")
    @Mapping(
            target = "initiatedAt",
            source = "createdAt",
            dateFormat = "yyyy-MM-dd HH:mm:ss"
    )
    @Mapping(
            target = "customerName",
            source = "order.user.name",
            qualifiedByName = "csvSafe"
    )
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(
            target = "reason",
            source = "reason",
            qualifiedByName = "csvSafe"
    )
    @Mapping(
            target = "originType",
            source = "originType",
            qualifiedByName = "csvSafe"
    )
    @Mapping(target = "status", source = "status")
    ReturnRequestCsvRow toCsvRow(ReturnRequest returnRequest);

    @Named("csvSafe")
    default String csvSafe(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String normalizedValue = value.stripLeading();

        // neu bat dau bang cac ki tu ben duoi-> them dau ', de tranh hieu la cong thuc
        if (!normalizedValue.isEmpty()
                && "=+-@".indexOf(normalizedValue.charAt(0)) >= 0) {
            return "'" + value;
        }

        return value;
    }
}
