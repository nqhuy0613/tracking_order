package com.me.tracking_order.order.dto.admin.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BulkConfirmOrderResponse {

    private int confirmedCount;

}