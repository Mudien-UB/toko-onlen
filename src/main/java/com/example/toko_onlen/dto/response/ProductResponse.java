package com.example.toko_onlen.dto.response;

import com.example.toko_onlen.entity.Product;
import com.example.toko_onlen.util.UuidParseUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProductResponse {

    private String id;
    private String name;
    private String category;
    private String description;
    private double price;
    private int stock;

    public static ProductResponse of(Product product) {
        if(product == null) return null;
        return ProductResponse.builder()
                .id(UuidParseUtil.uuidToString(product.getId()))
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription() == null || product.getDescription().isBlank() ? null : product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}
