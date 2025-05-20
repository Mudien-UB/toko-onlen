package com.example.toko_onlen.service.impl;

import com.example.toko_onlen.entity.Product;
import com.example.toko_onlen.exception.ResourceNotFoundException;
import com.example.toko_onlen.repository.ProductRepository;
import com.example.toko_onlen.service.ProductService;
import com.example.toko_onlen.util.BigDecimalParseUtil;
import com.example.toko_onlen.util.UuidParseUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(String name, String category, String description, BigDecimal price, int stock) {
        BigDecimalParseUtil.validatePositive(price, "Price must be positive");
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        Product newProduct = Product.builder()
                .name(name)
                .category(category)
                .description(description)
                .price(price)
                .stock(stock)
                .build();

        return productRepository.save(newProduct);
    }


    @Override
    public Product updateProduct(String id, String name, String category, String description, BigDecimal price, Integer stock) {
        UUID uuid = UuidParseUtil.stringToUuid(id);

        if (price != null) {
            BigDecimalParseUtil.validatePositive(price, "Price must be positive");
        }
        if (stock != null && stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        Product product = productRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setName(isBlank(name) ? product.getName() : name);
        product.setCategory(isBlank(category) ? product.getCategory() : category);
        product.setDescription(isBlank(description) ? product.getDescription() : description);
        product.setPrice(price == null ? product.getPrice() : price);
        product.setStock(stock == null ? product.getStock() : stock);

        return productRepository.save(product);
    }


    @Override
    public void deleteProduct(String id) {
        UUID uuid = UuidParseUtil.stringToUuid(id);
        Product product = productRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    public Product getProduct(String id) {
        UUID uuid = UuidParseUtil.stringToUuid(id);
        Product product = productRepository.findById(uuid).get();
        if(product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        return product;
    }

    @Override
    public Page<Product> listProductsPageable(String search, Integer page, Integer pageSize, String sortBy, String sortOrder) {
        int validPage = (page == null || page < 1) ? 0 : page - 1;
        int validPageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;

        Sort sort = Sort.by(sortBy == null || sortBy.isBlank() ? "price" : sortBy);
        sort = "desc".equalsIgnoreCase(sortOrder) ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(validPage, validPageSize, sort);

        Page<Product> result = (search == null || search.isBlank()) ?
                productRepository.findAll(pageable) :
                productRepository.findByNameContainingIgnoreCase(search, pageable);

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No products found");
        }

        return result;
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
