package com.agbunag.controller;

import com.agbunag.model.Order;
import com.agbunag.model.OrderItems;
import com.agbunag.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Add item to cart and update stock
    @PostMapping("/cart/add")
    public ResponseEntity<?> addItemToCart(@RequestBody OrderItems item, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first");
        }

        try {
            Order updatedCart = orderService.addItemToCart(customerId, item);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception ex) {
            log.error("Failed to add item to cart: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Remove item from cart and update stock
    @DeleteMapping("/cart/remove")
    public ResponseEntity<?> removeItemFromCart(@RequestParam int productId, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first");
        }

        try {
            Order updatedCart = orderService.removeItemFromCart(customerId, productId);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception ex) {
            log.error("Failed to remove item from cart: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Update item quantity in cart
    @PutMapping("/cart/update")
    public ResponseEntity<?> updateItemQuantity(@RequestParam int productId, @RequestParam double quantity, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first");
        }

        try {
            Order updatedCart = orderService.updateItemQuantity(customerId, productId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception ex) {
            log.error("Failed to update item quantity in cart: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // View cart
    @GetMapping("/cart/view")
    public ResponseEntity<?> viewCart(HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first");
        }

        try {
            Order cart = orderService.getCartByCustomerId(customerId);
            return ResponseEntity.ok(cart);
        } catch (Exception ex) {
            log.error("Failed to retrieve cart: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Clear cart and restore stock
    @DeleteMapping("/cart/clear")
    public ResponseEntity<?> clearCart(HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first");
        }

        try {
            orderService.clearCart(customerId);
            return ResponseEntity.ok("Cart cleared successfully.");
        } catch (Exception ex) {
            log.error("Failed to clear cart: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Checkout cart
    @PostMapping("/cart/checkout")
    public ResponseEntity<?> checkoutCart(@RequestBody Order order, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first");
        }

        log.info("Received checkout request for customerId {}: {}", customerId, order);

        try {
            order.setCustomerId(customerId); // Set the customerId to ensure correct customer is being checked out
            Order completedOrder = orderService.generateAndSaveInvoice(order);
            if (completedOrder != null) {
                return ResponseEntity.ok(completedOrder);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart not found or already checked out.");
            }
        } catch (Exception ex) {
            log.error("Failed to checkout cart: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during checkout.");
        }
    }
}
