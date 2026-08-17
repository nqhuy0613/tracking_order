package com.me.tracking_order.catalog.dto.admin.response;

import com.alibaba.excel.annotation.ExcelProperty;
import com.me.tracking_order.catalog.enums.StockStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductVariantCsvRow {

    @ExcelProperty(value = "Product Variant ID",index = 0)
    private String id;

    @ExcelProperty(value = "Name", index = 1)
    private String name;

    @ExcelProperty(value = "SKU", index = 2)
    private String sku;

    @ExcelProperty(value = "Unit Price", index = 3)
    private BigDecimal unitPrice;

    @ExcelProperty(value = "Quantity In Stock", index = 4)
    private Integer quantityInStock;

    @ExcelProperty(value = "Stock Status", index = 5)
    private StockStatus stockStatus;
}
