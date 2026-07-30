package com.me.tracking_order.returns.dto.customer.response;

import com.me.tracking_order.returns.enums.ReturnRequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnRequestResponse {

    private String id;

    private String reason;

    private String originType;

    private ReturnRequestStatus status;

    private String orderId;

    private String customerName;
}
