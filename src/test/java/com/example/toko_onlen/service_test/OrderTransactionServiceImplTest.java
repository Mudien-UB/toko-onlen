package com.example.toko_onlen.service_test;

import com.example.toko_onlen.model.entity.*;
import com.example.toko_onlen.model.enums.ORDER_STATUS;
import com.example.toko_onlen.model.record.ProductsOrder;
import com.example.toko_onlen.repository.OrderDetailRepository;
import com.example.toko_onlen.repository.OrderRepository;
import com.example.toko_onlen.repository.ProductRepository;
import com.example.toko_onlen.repository.UserRepository;
import com.example.toko_onlen.service.impl.OrderTransactionServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderTransactionServiceImplTest {

    @InjectMocks
    private OrderTransactionServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    private UUID userId;
    private UUID productId;
    private User user;
    private Product product;
    private ProductsOrder productsOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        user = User.builder().id(userId).build();
        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .stock(10)
                .price(BigDecimal.valueOf(100))
                .build();
        productsOrder = new ProductsOrder(productId, 2);
    }

    // ✅ Positive Test: Check Out Order
    @Test
    void testCheckOutOrder_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderDetailRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        Order order = orderService.checkOutOrder(userId.toString(), List.of(productsOrder));

        assertNotNull(order);
        assertEquals(ORDER_STATUS.PENDING, order.getStatus());
        assertEquals(1, order.getOrderDetails().size());
        assertEquals(BigDecimal.valueOf(200), order.getGrandTotal());
    }

    @Test
    void testCheckOutOrder_MultipleSameProduct_QuantityAggregated() {
        ProductsOrder po1 = new ProductsOrder(productId, 2);
        ProductsOrder po2 = new ProductsOrder(productId, 3); // sama product

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderDetailRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        Order order = orderService.checkOutOrder(userId.toString(), List.of(po1, po2));

        assertEquals(1, order.getOrderDetails().size());
        assertEquals(5, order.getOrderDetails().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(500), order.getGrandTotal());
    }



    // ❌ Negative Test: User ID null
    @Test
    void testCheckOutOrder_UserIdNull_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.checkOutOrder(null, List.of(productsOrder)));
        assertEquals("User cannot be null", ex.getMessage());
    }

    // ❌ Negative Test: Produk tidak ditemukan
    @Test
    void testCheckOutOrder_ProductNotFound_ShouldThrow() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> orderService.checkOutOrder(userId.toString(), List.of(productsOrder)));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void testCheckOutOrder_QuantityNegative_ShouldThrow() {
        ProductsOrder invalidOrder = new ProductsOrder(productId, -2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                orderService.checkOutOrder(userId.toString(), List.of(invalidOrder)));

        assertEquals("Quantity must be greater than 0", ex.getMessage());
    }


    // ✅ Positive Test: Confirm Order
    @Test
    void testConfirmOrder_Success() {
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(ORDER_STATUS.PENDING)
                .orderDetails(List.of(OrderDetail.builder()
                        .product(product)
                        .quantity(2)
                        .build()))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order confirmedOrder = orderService.confirmOrder(userId.toString(), 1L);

        assertEquals(ORDER_STATUS.SUCCESS, confirmedOrder.getStatus());
    }
    @Test
    void testConfirmOrder_UserNotFound_ShouldThrow() {
        Order order = Order.builder()
                .id(6L)
                .user(user)
                .status(ORDER_STATUS.PENDING)
                .build();

        when(orderRepository.findById(6L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                orderService.confirmOrder(userId.toString(), 6L));

        assertEquals("User not found", ex.getMessage());
    }


    // ❌ Negative Test: Confirm Order, Stok tidak cukup
    @Test
    void testConfirmOrder_StockNotEnough_ShouldThrow() {
        product.setStock(1); // stock < quantity
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(ORDER_STATUS.PENDING)
                .orderDetails(List.of(OrderDetail.builder()
                        .product(product)
                        .quantity(2)
                        .build()))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.confirmOrder(userId.toString(), 1L));
        assertTrue(ex.getMessage().contains("Stok tidak cukup"));
    }
    @Test
    void testConfirmOrder_AlreadySuccess_ShouldThrow() {
        Order order = Order.builder()
                .id(3L)
                .user(user)
                .status(ORDER_STATUS.SUCCESS) // sudah selesai
                .build();

        when(orderRepository.findById(3L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        EntityExistsException ex = assertThrows(EntityExistsException.class, () ->
                orderService.confirmOrder(userId.toString(), 3L));

        assertEquals("Order status is not PENDING", ex.getMessage());
    }

    @Test
    void testConfirmOrder_ProductNullInOrderDetail_ShouldThrow() {
        OrderDetail detail = OrderDetail.builder().product(null).quantity(2).build();

        Order order = Order.builder()
                .id(5L)
                .user(user)
                .status(ORDER_STATUS.PENDING)
                .orderDetails(List.of(detail))
                .build();

        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                orderService.confirmOrder(userId.toString(), 5L));
    }


    // ✅ Positive Test: Cancel Order
    @Test
    void testCancelOrder_Success() {
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(ORDER_STATUS.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order canceled = orderService.cancelOrder(userId.toString(), 1L);

        assertEquals(ORDER_STATUS.CANCELED, canceled.getStatus());
    }

    @Test
    void testConfirmOrder_ExactStock_Success() {
        product.setStock(2); // stock == quantity

        Order order = Order.builder()
                .id(2L)
                .user(user)
                .status(ORDER_STATUS.PENDING)
                .orderDetails(List.of(OrderDetail.builder()
                        .product(product)
                        .quantity(2)
                        .build()))
                .build();

        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order confirmed = orderService.confirmOrder(userId.toString(), 2L);

        assertEquals(ORDER_STATUS.SUCCESS, confirmed.getStatus());
        assertEquals(0, product.getStock());
    }


    // ❌ Negative Test: Cancel Order, Bukan order milik user
    @Test
    void testCancelOrder_WrongUser_ShouldThrow() {
        User otherUser = User.builder().id(UUID.randomUUID()).build();
        Order order = Order.builder().id(1L).user(otherUser).status(ORDER_STATUS.PENDING).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.cancelOrder(userId.toString(), 1L));
        assertEquals("Its not your order", ex.getMessage());
    }
    @Test
    void testCancelOrder_AlreadyCanceled_ShouldThrow() {
        Order order = Order.builder()
                .id(4L)
                .user(user)
                .status(ORDER_STATUS.CANCELED)
                .build();

        when(orderRepository.findById(4L)).thenReturn(Optional.of(order));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        EntityExistsException ex = assertThrows(EntityExistsException.class, () ->
                orderService.cancelOrder(userId.toString(), 4L));

        assertEquals("Order status is not PENDING", ex.getMessage());
    }


}
