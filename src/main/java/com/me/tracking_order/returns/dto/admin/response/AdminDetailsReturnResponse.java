package com.me.tracking_order.returns.dto.admin.response;

import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminDetailsReturnResponse {

    private String id;

    private String reason;

    private String originType;

    private ReturnRequestStatus status;

    private String orderId;

    private String customerName;

    private List<ReturnLogResponse> returnLogs;

}