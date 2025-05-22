package com.example.toko_onlen.dto.response;

import com.example.toko_onlen.model.entity.Product;
import com.example.toko_onlen.util.UuidParseUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse {

    private String id;
    private String name;
    private String category;
    private String description;
    private String price;
    private int stock;

    public static ProductResponse of(Product product) {
        if (product == null) return null;
        return ProductResponse.builder()
                .id(UuidParseUtil.uuidToString(product.getId()))
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription() != null && !product.getDescription().isBlank() ? product.getDescription() : null)
                .price(product.getPrice().toPlainString())
                .stock(product.getStock())
                .build();
    }
}
