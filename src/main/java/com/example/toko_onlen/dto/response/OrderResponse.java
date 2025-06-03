package com.example.toko_onlen.dto.response;

import com.example.toko_onlen.model.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class OrderResponse {

    private Long id;
    private String status;
    private UserResponse user;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static OrderResponse of(Order order) {
        if(order == null) return null;
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus().toString())
                .user(UserResponse.of(order.getUser()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
