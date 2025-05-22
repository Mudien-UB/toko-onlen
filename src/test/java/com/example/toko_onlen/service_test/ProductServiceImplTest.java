package com.example.toko_onlen.service_test;

import com.example.toko_onlen.model.entity.Product;
import com.example.toko_onlen.repository.ProductRepository;
import com.example.toko_onlen.service.impl.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct_success() {
        Product product = Product.builder()
                .name("Test Product")
                .category("Category")
                .description("Desc")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.createProduct("Test Product", "Category", "Desc", BigDecimal.valueOf(100.0), 10);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testCreateProduct_withInvalidData() {
        assertThrows(IllegalArgumentException.class, () ->
                productService.createProduct("Test", "Cat", "Desc", BigDecimal.valueOf(-10.0), 5));
    }

    @Test
    void testUpdateProduct_success() {
        UUID id = UUID.randomUUID();
        String idStr = id.toString();
        Product existingProduct = Product.builder()
                .id(id)
                .name("Old Name")
                .category("Old Category")
                .description("Old Description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product updatedProduct = productService.updateProduct(
                idStr, "New Name", null, "", BigDecimal.valueOf(150.0), null
        );

        assertNotNull(updatedProduct);
        assertEquals("New Name", updatedProduct.getName());
        assertEquals("Old Category", updatedProduct.getCategory());
        assertEquals("Old Description", updatedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(150.0), updatedProduct.getPrice());
        assertEquals(10, updatedProduct.getStock());

        verify(productRepository).findById(id);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void testUpdateProduct_notFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                productService.updateProduct(id.toString(), "Name", "Cat", "Desc", BigDecimal.valueOf(10.0), 1));
    }

    @Test
    void testUpdateProduct_withInvalidIdFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                productService.updateProduct("not-a-uuid", "Name", "Cat", "Desc", BigDecimal.valueOf(10.0), 1));
    }

    @Test
    void testUpdateProduct_withInvalidPriceFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                productService.updateProduct(UUID.randomUUID().toString(), "Name", "Cat", "Desc", BigDecimal.valueOf(-1000.2), 1));
    }

    @Test
    void testDeleteProduct_success() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder().build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.deleteProduct(id.toString());

        verify(productRepository).delete(product);
    }

    @Test
    void testDeleteProduct_notFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteProduct(id.toString()));
    }

    @Test
    void testDeleteProduct_withInvalidIdFormat() {
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct("not-a-uuid"));
    }

    @Test
    void testGetProduct_success() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder().name("Prod").build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product result = productService.getProduct(id.toString());

        assertEquals("Prod", result.getName());
    }

    @Test
    void testGetProduct_notFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> productService.getProduct(id.toString()));
    }

    @Test
    void testGetProduct_withInvalidIdFormat() {
        assertThrows(IllegalArgumentException.class, () -> productService.getProduct("not-a-uuid"));
    }

    @Test
    void testListProductsPageable_withoutSearch() {
        Page<Product> page = new PageImpl<>(List.of(Product.builder().build()));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.listProductsPageable(null, 0, 10, "price", "asc");

        assertEquals(1, result.getContent().size());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void testListProductsPageable_withSearch() {
        Page<Product> page = new PageImpl<>(List.of(Product.builder().build()));
        when(productRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.listProductsPageable("test", 0, 10, "price", "asc");

        assertEquals(1, result.getContent().size());
        verify(productRepository).findByNameContainingIgnoreCase(eq("test"), any(Pageable.class));
    }

    @Test
    void testListProductsPageable_withNegativePageNumber() {
        Page<Product> page = new PageImpl<>(List.of(Product.builder().build()));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.listProductsPageable(null, -5, 10, "price", "asc");

        assertEquals(1, result.getContent().size());
    }

    @Test
    void testListProductsPageable_withInvalidSortOrder() {
        Page<Product> page = new PageImpl<>(List.of(Product.builder().build()));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.listProductsPageable(null, 0, 10, "price", "invalidOrder");

        assertEquals(1, result.getContent().size());
    }
}
