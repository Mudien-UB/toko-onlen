package com.example.toko_onlen.model.seeder;

import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.repository.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.toko_onlen.model.entity.Product;
import com.example.toko_onlen.repository.ProductRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Locale;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final Faker faker = new Faker(new Locale("id_ID"));

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            for (int i = 0; i < 20; i++) {
                Product product = Product.builder()
                        .name(faker.commerce().productName() + " " + faker.number().randomDigit())
                        .category(faker.commerce().department())
                        .description(faker.lorem().sentence(10))
                        .price(BigDecimal.valueOf(Double.parseDouble(faker.commerce().price(10000.0, 100000.0))))
                        .stock(faker.number().numberBetween(5, 100))
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .updatedAt(new Timestamp(System.currentTimeMillis()))
                        .build();

                productRepository.save(product);
            }
            System.out.println("20 Produk dummy berhasil di-seed!");
        }
        if(userRepository.count() == 0) {
            for (int i = 0; i < 10; i++) {
                User user = User.builder()
                        .username(faker.name().username())
                        .build();
                userRepository.save(user);
            }
        }
        System.out.println("10 User dummy berhasil di-seed!");
    }
}
