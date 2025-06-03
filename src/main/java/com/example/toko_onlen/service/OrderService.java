package com.example.toko_onlen.service;

import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.model.enums.ORDER_STATUS;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Order getOrderById(String id);
    Page<Order> getAllOrderPageable(Integer page, Integer pageSize, String sortBy, String sortOrder, ORDER_STATUS orderStatus, User user);

}