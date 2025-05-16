package com.example.toko_onlen.util;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.toko_onlen.entity.Product;
import com.example.toko_onlen.repository.ProductRepository;

import java.sql.Timestamp;
import java.util.Locale;

@Profile("dev")
@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final Faker faker = new Faker(new Locale("id_ID"));

    public DatabaseSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            for (int i = 0; i < 20; i++) {
                Product product = Product.builder()
                        .name(faker.commerce().productName() + " " + faker.number().randomDigit())
                        .category(faker.commerce().department())
                        .description(faker.lorem().sentence(10))
                        .price(Double.valueOf(faker.commerce().price(10000.0, 100000.0)))
                        .stock(faker.number().numberBetween(5, 100))
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .updatedAt(new Timestamp(System.currentTimeMillis()))
                        .build();

                productRepository.save(product);
            }
            System.out.println("20 Produk dummy berhasil di-seed!");
        }
    }
}
