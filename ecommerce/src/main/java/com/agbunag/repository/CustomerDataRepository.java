package com.agbunag.repository;

import com.agbunag.entity.CustomerData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerDataRepository extends JpaRepository<CustomerData, Integer> {
    Optional<CustomerData> findByUsernameAndPassword(String username, String password); // Add this method
}
