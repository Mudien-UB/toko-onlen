package com.example.toko_onlen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @PrePersist
    public void prePersist() {
        if (product != null && product.getPrice() != null) {
            total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (product != null && product.getPrice() != null) {
            total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }


}

