package com.example.toko_onlen.service_test;


import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.entity.OrderDetail;
import com.example.toko_onlen.repository.OrderDetailRepository;
import com.example.toko_onlen.service.impl.OrderDetailServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderDetailServiceImplTest {

    private OrderDetailRepository orderDetailRepository;
    private OrderDetailServiceImpl orderDetailService;

    @BeforeEach
    void setUp() {
        orderDetailRepository = mock(OrderDetailRepository.class);
        orderDetailService = new OrderDetailServiceImpl(orderDetailRepository);
    }

    @Test
    void testGetOrderDetailById_ValidId_ReturnsDetail() {
        OrderDetail detail = new OrderDetail();
        detail.setId(1L);

        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(detail));

        OrderDetail result = orderDetailService.getOrderDetailById("1");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetOrderDetailById_Null_ThrowsIllegalArgumentException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> orderDetailService.getOrderDetailById(null));
        assertEquals("OrderDetail id cannot be null or blank", ex.getMessage());
    }

    @Test
    void testGetOrderDetailById_Blank_ThrowsIllegalArgumentException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> orderDetailService.getOrderDetailById("   "));
        assertEquals("OrderDetail id cannot be null or blank", ex.getMessage());
    }

    @Test
    void testGetOrderDetailById_InvalidFormat_ThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> orderDetailService.getOrderDetailById("abc"));
    }

    @Test
    void testGetOrderDetailById_NotFound_ThrowsEntityNotFoundException() {
        when(orderDetailRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> orderDetailService.getOrderDetailById("99"));
        assertEquals("OrderDetail id not found", ex.getMessage());
    }

    @Test
    void testGetOrderDetailByOrderId_Valid_ReturnsList() {
        Order order = new Order();
        order.setId(5L);

        OrderDetail detail = new OrderDetail();
        detail.setId(10L);
        detail.setOrder(order);
        detail.setQuantity(2);

        when(orderDetailRepository.findByOrderId(5L)).thenReturn(List.of(detail));

        List<OrderDetail> result = orderDetailService.getOrderDetailByOrderId("5");

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void testGetOrderDetailByOrderId_Null_ThrowsIllegalArgumentException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> orderDetailService.getOrderDetailByOrderId(null));
        assertEquals("OrderDetail id cannot be null or blank", ex.getMessage());
    }

    @Test
    void testGetOrderDetailByOrderId_Blank_ThrowsIllegalArgumentException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> orderDetailService.getOrderDetailByOrderId(" "));
        assertEquals("OrderDetail id cannot be null or blank", ex.getMessage());
    }

    @Test
    void testGetOrderDetailByOrderId_InvalidFormat_ThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> orderDetailService.getOrderDetailByOrderId("id123"));
    }

    @Test
    void testGetOrderDetailByOrderId_NotFound_ThrowsEntityNotFoundException() {
        when(orderDetailRepository.findByOrderId(88L)).thenReturn(Collections.emptyList());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> orderDetailService.getOrderDetailByOrderId("88"));
        assertEquals("OrderDetail id not found", ex.getMessage());
    }
}
