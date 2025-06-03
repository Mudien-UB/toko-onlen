package com.example.toko_onlen.repository;

import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.enums.ORDER_STATUS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByUserId(UUID userId);

    Page<Order> findByStatus(ORDER_STATUS status, Pageable pageable);
}
