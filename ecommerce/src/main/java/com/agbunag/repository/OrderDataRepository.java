package com.agbunag.repository;

import com.agbunag.model.Order;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.List;

public interface OrderDataRepository extends CrudRepository<Order, Integer> {

    // Custom query to find an active cart for a customer
        Optional<Order> findByCustomerIdAndStatus(int customerId, String status);

    // Purchased orders (multiple)
    List<Order> findAllByCustomerIdAndStatus(int customerId, String status);
}
