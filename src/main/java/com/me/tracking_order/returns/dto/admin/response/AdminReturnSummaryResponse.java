package com.me.tracking_order.returns.dto.admin.response;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdminReturnSummaryResponse {
    private long activeReturns;

    private long awaitingInspection;

    private BigDecimal totalRefunds;

    public AdminReturnSummaryResponse(
            Long activeReturns,
            Long awaitingInspection,
            BigDecimal totalRefunds
    ) {
        this.activeReturns =
                activeReturns == null ? 0L : activeReturns;

        this.awaitingInspection =
                awaitingInspection == null ? 0L : awaitingInspection;

        this.totalRefunds =
                totalRefunds == null ? BigDecimal.ZERO : totalRefunds;
    }
}
