package com.example.toko_onlen.service_test;

import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.model.enums.ORDER_STATUS;
import com.example.toko_onlen.repository.OrderRepository;
import com.example.toko_onlen.service.impl.OrderServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private OrderRepository orderRepository;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderServiceImpl(orderRepository);
    }

    // ========== getOrderById ==========

    @Test
    void getOrderById_ShouldReturnOrder_WhenValidId() {
        Order order = Order.builder()
                .id(1L)
                .grandTotal(BigDecimal.TEN)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById("1");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenZeroPaddedId() {
        Order order = Order.builder().id(5L).build();
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById("0000005");

        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenIdIsNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                orderService.getOrderById(null));
        assertEquals("Order ID cannot be null or empty", ex.getMessage());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                orderService.getOrderById("  "));
    }

    @Test
    void getOrderById_ShouldThrowException_WhenIdFormatInvalid() {
        assertThrows(NumberFormatException.class, () ->
                orderService.getOrderById("abc"));
    }

    @Test
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () ->
                orderService.getOrderById("99"));
        assertTrue(ex.getMessage().contains("Order with ID: 99 not found"));
    }

    // ========== getAllOrderPageable ==========
    @Test
    void getAllOrderPageable_ShouldReturnOrders_WhenNoStatusGiven() {
        Order order = new Order();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("grandTotal"));
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Order> result = orderService.getAllOrderPageable(1, 10, "grandTotal", "asc", null, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllOrderPageable_ShouldReturnFilteredOrders_WhenStatusGiven() {
        Order order = new Order();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("grandTotal").ascending());
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Order> result = orderService.getAllOrderPageable(1, 10, "grandTotal", "asc", ORDER_STATUS.SUCCESS, null);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllOrderPageable_ShouldSortDescending_WhenSortOrderIsDesc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("grandTotal").descending());
        Page<Order> page = new PageImpl<>(List.of(new Order()));

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Order> result = orderService.getAllOrderPageable(1, 10, "grandTotal", "desc", null, null);

        assertFalse(result.isEmpty());
    }

    @Test
    void getAllOrderPageable_ShouldUseDefaultPagination_WhenInvalidParams() {
        Page<Order> page = new PageImpl<>(List.of(new Order()));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Order> result = orderService.getAllOrderPageable(null, null, null, null, null, null);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllOrderPageable_ShouldThrowException_WhenNoOrdersFound() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("grandTotal"));
        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.getAllOrderPageable(1, 10, "grandTotal", "asc", null, null));
    }
}
