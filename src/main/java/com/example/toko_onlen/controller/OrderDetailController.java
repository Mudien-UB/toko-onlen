package com.example.toko_onlen.controller;

import com.example.toko_onlen.dto.common.CustomizeResponseEntity;
import com.example.toko_onlen.dto.response.OrderDetailResponse;
import com.example.toko_onlen.exception.BadRequestException;
import com.example.toko_onlen.exception.ResourceNotFoundException;
import com.example.toko_onlen.model.entity.OrderDetail;
import com.example.toko_onlen.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order/details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String id) {
        if(!validateId(id)) {
            throw new BadRequestException("Order id is invalid");
        }

        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        if(orderDetail == null) {
            throw new ResourceNotFoundException("Order id not found");
        }
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "Order Details Retrieved",
                OrderDetailResponse.of(orderDetail)
        );
    }

    @GetMapping
    public ResponseEntity<?> getOrderDetailsByOrderId(@RequestParam String orderId) {
        if(!validateId(orderId)) {
            throw new BadRequestException("Order id is invalid");
        }
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(orderId);
        if(orderDetails == null) {
            throw new ResourceNotFoundException("Order id not found");
        }
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "List Order Details Retrieved",
                orderDetails.stream().map(OrderDetailResponse::of).toList()
        );
    }



    private boolean validateId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        try {
            long parsedId = Long.parseLong(id);
            return parsedId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
