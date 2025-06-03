package com.example.toko_onlen.repository;

import com.example.toko_onlen.model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {


    List<OrderDetail> findByOrderId(Long orderId);
}
