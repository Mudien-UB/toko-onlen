package com.example.toko_onlen.service.impl;

import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.entity.OrderDetail;
import com.example.toko_onlen.model.entity.Product;
import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.model.enums.ORDER_STATUS;
import com.example.toko_onlen.model.record.ProductsOrder;
import com.example.toko_onlen.repository.OrderDetailRepository;
import com.example.toko_onlen.repository.OrderRepository;
import com.example.toko_onlen.repository.ProductRepository;
import com.example.toko_onlen.repository.UserRepository;
import com.example.toko_onlen.service.OrderTransactionService;
import com.example.toko_onlen.util.UuidParseUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderTransactionServiceImpl implements OrderTransactionService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Order checkOutOrder(String userId, List<ProductsOrder> productsOrders) {
        if (userId == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (productsOrders == null || productsOrders.isEmpty()) {
            throw new IllegalArgumentException("Products orders cannot be null or empty");
        }


        User user = userRepository.findById(UuidParseUtil.stringToUuid(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Map<Product, Integer> listProduct = productsOrders.stream()
                .map(po -> {
                    if(po.quantity() <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");

                    Product product = productRepository.findById(po.productId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                    if(product.getStock() < po.quantity()) throw new IllegalArgumentException("Stok tidak cukup untuk produk:" + product.getName());

                    return new AbstractMap.SimpleEntry<>(product, po.quantity());
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum
                ));

        Order order = Order.builder()
                .user(user)
                .status(ORDER_STATUS.PENDING)
                .build();

        List<OrderDetail> orderDetails = listProduct.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    Integer quantity = entry.getValue();
                    BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
                    return OrderDetail.builder()
                            .order(order)
                            .product(product)
                            .quantity(quantity)
                            .total(totalPrice)
                            .build();
                }).toList();

        BigDecimal grandTotalPrice = orderDetails.stream()
                .map(OrderDetail::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderDetails(orderDetails);
        order.setGrandTotal(grandTotalPrice);

        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);

        return order;
    }

    @Override
    public Order confirmOrder(String userId, Long orderId) {
        if (userId == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("Order Id cannot be null");
        }


        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        User user = userRepository.findById(UuidParseUtil.stringToUuid(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(!order.getUser().equals(user)) {
            throw new IllegalArgumentException("Its not your order");
        }

        if (order.getStatus() != ORDER_STATUS.PENDING) {
            throw new EntityExistsException("Order status is not PENDING");
        }

        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            if (product.getStock() < detail.getQuantity()) {
                throw new IllegalArgumentException("Stok tidak cukup untuk produk: " + product.getName());
            }
        }

        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() - detail.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(ORDER_STATUS.SUCCESS);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(String userId, Long orderId) {
        if (userId == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("Order Id cannot be null");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        User user = userRepository.findById(UuidParseUtil.stringToUuid(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(!order.getUser().equals(user)) {
            throw new IllegalArgumentException("Its not your order");
        }

        if (order.getStatus() != ORDER_STATUS.PENDING) {
            throw new EntityExistsException("Order status is not PENDING");
        }

        order.setStatus(ORDER_STATUS.CANCELED);
        return orderRepository.save(order);
    }

}
