package com.example.toko_onlen.service;

import com.example.toko_onlen.entity.Product;
import org.springframework.data.domain.Page;


public interface ProductService {

    Product createProduct(String name, String category, String description, double price, int stock);
    Product updateProduct(String id, String name, String category, String description, Double price, Integer stock);
    void deleteProduct(String id);
    Product getProduct(String id);
    Page<Product> listProductsPageable(String search, Integer page, Integer pageSize, String sortBy, String sortOrder);
}
