package com.example.toko_onlen.controller;

import com.example.toko_onlen.dto.common.CustomizeResponseEntity;
import com.example.toko_onlen.dto.response.OrderResponse;
import com.example.toko_onlen.dto.response.ProductResponse;
import com.example.toko_onlen.exception.ResourceNotFoundException;
import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.model.enums.ORDER_STATUS;
import com.example.toko_onlen.service.OrderService;
import com.example.toko_onlen.service.UserService;
import com.example.toko_onlen.util.UuidParseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        if (!validateId(id)) {
            throw new IllegalArgumentException("Order ID is invalid");
        }

        Order response = orderService.getOrderById(id);
        if (response == null) {
            throw new ResourceNotFoundException("Order Not Found");
        }

        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "Order retrieved successfully",
                OrderResponse.of(response)
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllOrderPageable(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = true) String userId
    ) {

//        User user = AuthProvider.getUser;
        User user = userService.getUserById(UuidParseUtil.stringToUuid(userId));

        Page<Order> response = orderService.getAllOrderPageable(page, pageSize, sortBy, sortOrder, ORDER_STATUS.fromString(orderStatus), user);

        if (response == null || response.isEmpty()) {
            throw new ResourceNotFoundException("Order Not Found");
        }
        return CustomizeResponseEntity.buildResponsePageable(
                HttpStatus.OK,
                "Product List Retrieved",
                response.getContent().stream().map(OrderResponse::of).collect(Collectors.toList()),
                response.getNumber(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages()
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
