package com.agbunag.service;

import com.agbunag.model.Order;
import com.agbunag.model.OrderItems;

public interface OrderService {

    // Existing methods for order processing
    Order create(Order order);
    Order invoice(Order order);
    Order pay(Order order);
    Order pick(Order order);
    Order ship(Order order);
    Order complete(Order order);
    Order cancel(Order order);
    Order suspend(Order order);
    Order update(Order order);
    Order checkout(Order orderRequest);

    // New methods for cart operations
    Order addItemToCart(int customerId, OrderItems item);
    Order removeItemFromCart(int customerId, int productId);
    Order updateItemQuantity(int customerId, int productId, double quantity);
    Order getCartByCustomerId(int customerId);
    void clearCart(int customerId);
    Order generateAndSaveInvoice(Order order);
}
