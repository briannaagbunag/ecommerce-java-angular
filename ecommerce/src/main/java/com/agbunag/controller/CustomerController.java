package com.agbunag.controller;

import com.agbunag.model.Customer;
import com.agbunag.model.Order;
import com.agbunag.service.CustomerService;
import com.agbunag.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    // Sign-Up Endpoint
    @PostMapping("/api/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        log.info("Received sign-up request for: " + customer.getUsername());
        try {
            Customer newCustomer = customerService.create(customer);
            log.info("Successfully created customer: " + newCustomer);
            return ResponseEntity.ok(newCustomer);
        } catch (Exception ex) {
            log.error("Failed to create customer", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Login Endpoint
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            Optional<Customer> customer = customerService.findByUsernameAndPassword(username, password);
            if (customer.isPresent()) {
                log.info("Login successful for user: " + username);

                // Store customerId in the session
                session.setAttribute("customerId", customer.get().getCustomerId());

                return ResponseEntity.ok(customer.get());
            } else {
                log.warn("Invalid credentials for user: " + username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception ex) {
            log.error("Login failed for user: " + username, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    // View Cart Endpoint
    @GetMapping("/cart/view")
    public ResponseEntity<?> viewCart(HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first");
        }

        try {
            Order cart = orderService.getCartByCustomerId(customerId); // Use OrderService to fetch the cart
            return ResponseEntity.ok(cart);
        } catch (Exception ex) {
            log.error("Failed to retrieve cart: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Get All Customers
    @GetMapping("/api/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.findAll();
            return ResponseEntity.ok(customers);
        } catch (Exception ex) {
            log.error("Failed to retrieve customers", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get Customer by ID
    @GetMapping("/api/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable int id) {
        try {
            Optional<Customer> customer = customerService.findById(id);
            return customer.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception ex) {
            log.error("Failed to retrieve customer with ID: " + id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Logout Endpoint
    @PostMapping("/api/auth/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate(); // Clear the session
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }


    // Check session endpoint
    @GetMapping("/api/auth/session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId != null) {
            Optional<Customer> customer = customerService.findById(customerId);
            if (customer.isPresent()) {
                return ResponseEntity.ok(customer.get());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
    }
}
