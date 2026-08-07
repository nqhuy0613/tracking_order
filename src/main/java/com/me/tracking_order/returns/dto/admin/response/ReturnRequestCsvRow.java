package com.me.tracking_order.returns.dto.admin.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnRequestCsvRow {

    @ExcelProperty(value = "Return ID", index = 0)
    private String returnId;

    @ExcelProperty(value = "Initiated At", index = 1)
    private String initiatedAt;

    @ExcelProperty(value = "Customer Name", index = 2)
    private String customerName;

    @ExcelProperty(value = "Order ID", index = 3)
    private String orderId;

    @ExcelProperty(value = "Reason", index = 4)
    private String reason;

    @ExcelProperty(value = "Origin Type", index = 5)
    private String originType;

    @ExcelProperty(value = "Status", index = 6)
    private String status;
}
