package com.example.toko_onlen.controller;

import com.example.toko_onlen.dto.common.CustomizeResponseEntity;
import com.example.toko_onlen.dto.mapper.OrderTransactionMapper;
import com.example.toko_onlen.dto.request.OrderTransactionRequest;
import com.example.toko_onlen.dto.response.OrderResponse;
import com.example.toko_onlen.dto.validation.OnCreate;
import com.example.toko_onlen.exception.BadRequestException;
import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.service.OrderTransactionService;
import com.example.toko_onlen.util.UuidParseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class OrderTransactionController {

    private final OrderTransactionService orderTransactionService;

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestParam String userId,
            @Validated(OnCreate.class) @RequestBody List<OrderTransactionRequest> orderTransactionRequest
    ) {
        if(userId == null || !UuidParseUtil.isValidUUID(userId) || orderTransactionRequest == null || orderTransactionRequest.isEmpty()) {
            throw new BadRequestException("Invalid request");
        }

        Order response = orderTransactionService.checkOutOrder(
                userId,
                orderTransactionRequest.stream().map(OrderTransactionMapper::toProductOrder).toList()
        );

        if(response == null) {
            throw new RuntimeException("Invalid response");
        }

        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "check out order success",
                OrderResponse.of(response)
        );

    }

    @PutMapping("/confirm/{orderId}")
    public ResponseEntity<?> confirmOrder(
            @PathVariable String orderId,
            @RequestParam String userId
    ){
        if(userId == null || !UuidParseUtil.isValidUUID(userId) || orderId == null || orderId.isEmpty() || !validateId(orderId)) {
            throw new BadRequestException("Invalid request");
        }
        Order response = orderTransactionService.confirmOrder(userId, Long.parseLong(orderId));
        if(response == null) {
            throw new RuntimeException("Invalid response");
        }
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "confirm order success",
                OrderResponse.of(response)
        );
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable String orderId,
            @RequestParam String userId
    ){
        if(userId == null || !UuidParseUtil.isValidUUID(userId) || orderId == null || orderId.isEmpty() || !validateId(orderId)) {
            throw new BadRequestException("Invalid request");
        }
        Order response = orderTransactionService.cancelOrder(userId, Long.parseLong(orderId));
        if(response == null) {
            throw new RuntimeException("Invalid response");
        }
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "cancel order success",
                OrderResponse.of(response)
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
