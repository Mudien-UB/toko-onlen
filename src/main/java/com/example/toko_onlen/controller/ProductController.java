package com.example.toko_onlen.controller;

import com.example.toko_onlen.dto.common.CustomizeResponseEntity;
import com.example.toko_onlen.dto.request.ProductRequest;
import com.example.toko_onlen.dto.response.ProductResponse;
import com.example.toko_onlen.dto.validation.OnCreate;
import com.example.toko_onlen.dto.validation.OnUpdate;
import com.example.toko_onlen.model.entity.Product;
import com.example.toko_onlen.exception.ResourceNotFoundException;
import com.example.toko_onlen.service.ProductService;
import com.example.toko_onlen.util.BigDecimalParseUtil;
import com.example.toko_onlen.util.UuidParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(@Validated(OnCreate.class) @RequestBody ProductRequest productRequest) {

        BigDecimalParseUtil.validatePositive(productRequest.getPrice());

        Product product = productService.createProduct(
                productRequest.getName(),
                productRequest.getCategory(),
                productRequest.getDescription(),
                BigDecimalParseUtil.strToBigDecimal(productRequest.getPrice(), "Invalid price format"),
                productRequest.getStock()
        );
        return CustomizeResponseEntity.buildResponse(HttpStatus.CREATED, "Product Created", ProductResponse.of(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @Validated(OnUpdate.class) @RequestBody ProductRequest productRequest) {

        validateUUID(id);
        if (isAllFieldNull(productRequest)) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }

        Product product = productService.updateProduct(
                id,
                productRequest.getName(),
                productRequest.getCategory(),
                productRequest.getDescription(),
                productRequest.getPrice() == null ? null : BigDecimalParseUtil.strToBigDecimal(productRequest.getPrice(), "Invalid price format"),
                productRequest.getStock()
        );
        return CustomizeResponseEntity.buildResponse(HttpStatus.OK, "Product Updated", ProductResponse.of(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        validateUUID(id);
        productService.deleteProduct(id);
        return CustomizeResponseEntity.buildResponse(HttpStatus.OK, "Product Deleted", null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        validateUUID(id);
        Product product = productService.getProduct(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        return CustomizeResponseEntity.buildResponse(HttpStatus.OK, "Product Found", ProductResponse.of(product));
    }

    @GetMapping("/list")
    public ResponseEntity<?> listProductsPageable(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {

        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (!Arrays.asList("name", "price", "stock").contains(sortBy.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sort parameter");
        }

        Page<Product> productPage = productService.listProductsPageable(keyword, page, size, sortBy, sortOrder);
        if (!productPage.hasContent()) {
            throw new ResourceNotFoundException("Product not found");
        }
        List<ProductResponse> content = productPage.getContent().stream().map(ProductResponse::of).collect(Collectors.toList());

        return CustomizeResponseEntity.buildResponsePageable(
                HttpStatus.OK,
                "Product List Retrieved",
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    private boolean isAllFieldNull(ProductRequest req) {
        return req.getName() == null && req.getCategory() == null &&
                req.getDescription() == null && req.getPrice() == null && req.getStock() == null;
    }

    private void validateUUID(String id) {
        if (!UuidParseUtil.isValidUUID(id)) {
            throw new IllegalArgumentException("Invalid Product ID");
        }
    }
}
