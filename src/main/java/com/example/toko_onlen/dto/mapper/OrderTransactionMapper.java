package com.example.toko_onlen.dto.mapper;

import com.example.toko_onlen.dto.request.OrderTransactionRequest;
import com.example.toko_onlen.model.record.ProductsOrder;
import com.example.toko_onlen.util.UuidParseUtil;

import java.util.UUID;

public class OrderTransactionMapper {

    public static ProductsOrder toProductOrder(OrderTransactionRequest req) {
        UUID productId = UuidParseUtil.stringToUuid(req.getProductId());
        return new ProductsOrder(productId, req.getQuantity());
    }

}
