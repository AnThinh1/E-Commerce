package com.codegym.ecommercemanage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockReportResponse {

    private Integer productId;
    private String productName;
    private Integer currentStock;
    private Integer totalImport;
    private Integer totalExport;
}
