package com.example.toko_onlen.service;

import com.example.toko_onlen.model.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    OrderDetail getOrderDetailById(String id);
    List<OrderDetail> getOrderDetailByOrderId(String orderId);
}
