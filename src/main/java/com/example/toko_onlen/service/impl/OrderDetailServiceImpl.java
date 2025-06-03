package com.example.toko_onlen.service.impl;

import com.example.toko_onlen.model.entity.OrderDetail;
import com.example.toko_onlen.repository.OrderDetailRepository;
import com.example.toko_onlen.service.OrderDetailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDetail getOrderDetailById(String id) {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("OrderDetail id cannot be null or blank");
        }
        try{
            long parseId = Long.parseLong(id);
            return orderDetailRepository.findById(parseId).orElseThrow(() -> new EntityNotFoundException("OrderDetail id not found"));
        }catch (NumberFormatException e){
            throw new NumberFormatException("OrderDetail id is not valid");
        }

    }

    @Override
    public List<OrderDetail> getOrderDetailByOrderId(String orderId) {
        if(orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("OrderDetail id cannot be null or blank");
        }
        try{
            long parseId = Long.parseLong(orderId);
            List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(parseId).stream().toList();

            if(orderDetailList.isEmpty()) {
                throw new EntityNotFoundException("OrderDetail id not found");
            }
            return orderDetailList;

        }catch (NumberFormatException e){
            throw new NumberFormatException("OrderDetail id is not valid");
        }
    }
}
