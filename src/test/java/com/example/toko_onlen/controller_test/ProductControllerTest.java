package com.example.toko_onlen.controller_test;

import com.example.toko_onlen.controller.ProductController;
import com.example.toko_onlen.dto.request.ProductRequest;
import com.example.toko_onlen.entity.Product;
import com.example.toko_onlen.exception.common.GlobalExceptionHandler;
import com.example.toko_onlen.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new TestRestExceptionHandler())
                .build();
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Kopi");
        product.setCategory("Minuman");
        product.setDescription("Kopi hitam");
        product.setPrice(15000.0);
        product.setStock(20);
        return product;
    }

    @Test
    void testAddProduct() throws Exception {
        ProductRequest request = new ProductRequest("Kopi", "Minuman", "Kopi hitam", 15000.0, 20);

        Product product = createSampleProduct();

        when(productService.createProduct(anyString(), anyString(), anyString(), anyDouble(), anyInt()))
                .thenReturn(product);

        mockMvc.perform(post("/product/add-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Product Created"))
                .andExpect(jsonPath("$.data.name").value("Kopi"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        String productId = UUID.randomUUID().toString();
        ProductRequest request = new ProductRequest("Kopi Update", "Minuman", "Kopi updated", 18000.0, 25);

        Product updatedProduct = createSampleProduct();
        updatedProduct.setName("Kopi Update");

        when(productService.updateProduct(eq(productId), anyString(), anyString(), anyString(), anyDouble(), anyInt()))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product Updated"))
                .andExpect(jsonPath("$.data.name").value("Kopi Update"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        String productId = UUID.randomUUID().toString();

        doNothing().when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/product/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product Deleted"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testGetProduct() throws Exception {
        String productId = UUID.randomUUID().toString();
        Product product = createSampleProduct();

        when(productService.getProduct(productId)).thenReturn(product);

        mockMvc.perform(get("/product/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product Found"))
                .andExpect(jsonPath("$.data.name").value("Kopi"));
    }

    @Test
    void testListProductsPageable() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(createSampleProduct()), PageRequest.of(0, 10), 1);

        when(productService.listProductsPageable("", 1, 10, "price", "asc")).thenReturn(page);

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product List Retrieved"));
    }

    @Test
    void testGetProduct_invalidIdFormat() throws Exception {
        mockMvc.perform(get("/product/abc-invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Product ID"));
    }

    @Test
    void testGetProduct_serviceThrowsException() throws Exception {
        String id = UUID.randomUUID().toString();

        when(productService.getProduct(id)).thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/product/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }

    @Test
    void testAddProduct_validationFails_emptyBody() throws Exception {
        mockMvc.perform(post("/product/add-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testAddProduct_invalidFields() throws Exception {
        ProductRequest request = new ProductRequest("", null, "Desc", -100.0, -5);

        mockMvc.perform(post("/product/add-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testUpdateProduct_invalidIdFormat() throws Exception {
        ProductRequest request = new ProductRequest("Nama", "Kategori", "Desc", 10000.0, 10);

        mockMvc.perform(put("/product/invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Product ID"));
    }

    @Test
    void testUpdateProduct_serviceThrowsException() throws Exception {
        String productId = UUID.randomUUID().toString();
        ProductRequest request = new ProductRequest("Nama", "Kategori", "Desc", 10000.0, 10);

        when(productService.updateProduct(eq(productId), anyString(), anyString(), anyString(), anyDouble(), anyInt()))
                .thenThrow(new EntityNotFoundException("Product not found"));

        mockMvc.perform(put("/product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void testDeleteProduct_serviceThrowsException() throws Exception {
        String productId = UUID.randomUUID().toString();

        doThrow(new EntityNotFoundException("Product not found")).when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/product/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void testListProductsPageable_with_negativePageParam() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(createSampleProduct()), PageRequest.of(0, 10), 1);

        when(productService.listProductsPageable("", 1, 10, "price", "asc")).thenReturn(page);
        mockMvc.perform(get("/product/list")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product List Retrieved"));
    }

    @Test
    void testListProductsPageable_notFound() throws Exception {
        when(productService.listProductsPageable("", 1, 10, "price", "asc")).thenReturn(Page.empty());

        mockMvc.perform(get("/product/list")
                        .param("page", "-1")
                        .param("size", "10")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void testGetProduct_notFound() throws Exception {
        String productId = UUID.randomUUID().toString();

        when(productService.getProduct(productId)).thenReturn(null);

        mockMvc.perform(get("/product/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProduct_emptyBody() throws Exception {
        String productId = UUID.randomUUID().toString();

        mockMvc.perform(put("/product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("At least one field must be provided for update"));
    }

    @Test
    void testListProductsPageable_invalidSortParam() throws Exception {
        mockMvc.perform(get("/product/list")
                        .param("sortBy", "invalidField"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid sort parameter"));
    }

    @Test
    void testAddProduct_serviceThrowsIllegalArgumentException() throws Exception {
        ProductRequest request = new ProductRequest("Kopi", "Minuman", "Kopi hitam", 15000.0, 20);

        when(productService.createProduct(anyString(), anyString(), anyString(), anyDouble(), anyInt()))
                .thenThrow(new IllegalArgumentException("Invalid Request"));

        mockMvc.perform(post("/product/add-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Request"));
    }

    @RestControllerAdvice
    static class TestRestExceptionHandler extends GlobalExceptionHandler {

    }
}
