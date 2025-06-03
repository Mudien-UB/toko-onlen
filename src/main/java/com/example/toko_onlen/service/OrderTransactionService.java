package com.example.toko_onlen.service;

import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.record.ProductsOrder;

import java.util.List;
import java.util.Map;

public interface OrderTransactionService {

    Order checkOutOrder(String userId, List<ProductsOrder>  orders);
    Order confirmOrder(String userId, Long  orderId);
    Order cancelOrder(String userId, Long  orderId);

}
