package com.agbunag.controller;

import com.agbunag.model.Product;
import com.agbunag.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    // Retrieve all products, including stock quantity
    @GetMapping("/api/products")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception ex) {
            log.error("Failed to retrieve products: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Retrieve product by ID, including stock quantity
    @GetMapping("/api/product/{id}")
    public ResponseEntity<?> getProductById(@PathVariable final Integer id) {
        try {
            Product product = productService.get(id);
            return ResponseEntity.ok(product);
        } catch (Exception ex) {
            log.error("Failed to retrieve product with ID: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Add a new product
    @PutMapping("/api/product")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            Product newProduct = productService.create(product);
            return ResponseEntity.ok(newProduct);
        } catch (Exception ex) {
            log.error("Failed to add product: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Update an existing product
    @PostMapping("/api/product")
    public ResponseEntity<?> updateProduct(@RequestBody Product product) {
        try {
            Product updatedProduct = productService.update(product);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception ex) {
            log.error("Failed to update product: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Delete a product
    @DeleteMapping("/api/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable final Integer id) {
        try {
            productService.delete(id);
            return ResponseEntity.ok(null);
        } catch (Exception ex) {
            log.error("Failed to delete product: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
