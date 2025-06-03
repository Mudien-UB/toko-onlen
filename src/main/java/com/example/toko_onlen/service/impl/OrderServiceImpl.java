package com.example.toko_onlen.service.impl;

import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.model.enums.ORDER_STATUS;
import com.example.toko_onlen.repository.OrderRepository;
import com.example.toko_onlen.repository.specification.OrderSpecification;
import com.example.toko_onlen.service.OrderService;
import com.example.toko_onlen.util.UuidParseUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Page<Order> getAllOrderPageable(Integer page, Integer pageSize, String sortBy, String sortOrder,  ORDER_STATUS orderStatus, User user) {

        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int size = (pageSize == null || pageSize <= 0) ? 10 : pageSize;

        Sort sort = Sort.by(sortBy == null || sortBy.isBlank() ? "grandTotal" : sortBy);
        sort = "desc".equalsIgnoreCase(sortOrder) ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(pageNumber, size, sort);

        Specification<Order> filter = Specification
                .where(OrderSpecification.hasStatus(orderStatus))
                .and(OrderSpecification.hasUser(user));

        Page<Order> listOrder = orderRepository.findAll(filter, pageable);

        if(listOrder.isEmpty()) throw new EntityNotFoundException("Order not found");

        return listOrder;

    }

    @Override
    public Order getOrderById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        try {
            long parsedId = Long.parseLong(id);
            return orderRepository.findById(parsedId)
                    .orElseThrow(() -> new EntityNotFoundException("Order with ID: " + id + " not found"));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid format for Order ID: " + id);
        }
    }

}
