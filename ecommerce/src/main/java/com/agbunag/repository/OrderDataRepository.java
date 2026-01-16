package com.agbunag.repository;

import com.agbunag.model.Order;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface OrderDataRepository extends CrudRepository<Order, Integer> {

    // Custom query to find an active cart for a customer
        Optional<Order> findByCustomerIdAndStatus(int customerId, String status);
}
