package com.example.toko_onlen.repository.specification;

import com.example.toko_onlen.model.entity.Order;
import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.model.enums.ORDER_STATUS;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;

public class OrderSpecification {

    public static Specification<Order> hasStatus(ORDER_STATUS status) {
        return (root, query, builder) ->
                status == null ? null : builder.equal(root.get("status"), status);
    }

    public static Specification<Order> hasUser(User user) {
        return (root, query, builder) ->
                user == null ? null : builder.equal(root.get("user"), user);
    }

}
