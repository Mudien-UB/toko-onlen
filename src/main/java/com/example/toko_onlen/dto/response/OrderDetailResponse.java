package com.example.toko_onlen.dto.response;

import com.example.toko_onlen.model.entity.OrderDetail;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class OrderDetailResponse {

    private Long orderId;
    private ProductResponse product;
    private int quantity;
    private BigDecimal priceTotal;

    public static OrderDetailResponse of(OrderDetail orderDetail) {
        if(orderDetail == null) return null;
        return OrderDetailResponse.builder()
                .orderId(orderDetail.getId())
                .product(ProductResponse.of(orderDetail.getProduct()))
                .quantity(orderDetail.getQuantity())
                .priceTotal(orderDetail.getTotal())
                .build();
    }

}
