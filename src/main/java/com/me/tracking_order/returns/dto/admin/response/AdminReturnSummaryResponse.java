package com.me.tracking_order.returns.dto.admin.response;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminReturnSummaryResponse {
    private long activeReturns;

    private long awaitingInspection;

    private BigDecimal totalRefunds;
}
