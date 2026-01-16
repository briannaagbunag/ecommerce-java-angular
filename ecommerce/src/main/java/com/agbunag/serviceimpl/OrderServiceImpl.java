package com.agbunag.serviceimpl;

import com.agbunag.entity.ProductData;
import com.agbunag.model.Order;
import com.agbunag.model.OrderItems;
import com.agbunag.repository.OrderDataRepository;
import com.agbunag.repository.ProductDataRepository;
import com.agbunag.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDataRepository orderDataRepository;

    @Autowired
    ProductDataRepository productDataRepository;

    @Override
    public Order create(Order order) {
        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }
        order.setStatus("active");
        return orderDataRepository.save(order);
    }

    @Override
    public Order invoice(Order order) {
        return null;
    }

    @Override
    public Order pay(Order order) {
        return null;
    }

    @Override
    public Order pick(Order order) {
        return null;
    }

    @Override
    public Order ship(Order order) {
        return null;
    }

    @Override
    public Order complete(Order order) {
        return null;
    }

    @Override
    public Order cancel(Order order) {
        return null;
    }

    @Override
    public Order suspend(Order order) {
        return null;
    }

    @Override
    public Order update(Order order) {
        return null;
    }

    // Add an item to the cart
    @Override
    public Order addItemToCart(int customerId, OrderItems item) {
        Order cart = getCartByCustomerId(customerId);
        if (cart == null) {
            cart = new Order();
            cart.setCustomerId(customerId);
            cart.setItems(new ArrayList<>());
            cart.setStatus("active");
        }

        Optional<ProductData> productOpt = productDataRepository.findById(item.getProductId());
        if (productOpt.isEmpty()) {
            log.warn("Product with ID {} not found.", item.getProductId());
            return null;
        }
        ProductData product = productOpt.get();

        if (product.getQuantityStock() < item.getQuantity()) {
            log.warn("Insufficient stock for product ID {}. Available: {}", product.getId(), product.getQuantityStock());
            return null;  // or handle insufficient stock scenario
        }

        // Reduce stock and save
        product.setQuantityStock(product.getQuantityStock() - (int) item.getQuantity());
        productDataRepository.save(product);

        boolean itemExists = false;
        for (OrderItems existingItem : cart.getItems()) {
            if (existingItem.getProductId() == item.getProductId()) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            item.setQuantityStock(product.getQuantityStock()); // Sync stock quantity
            item.setDescription(product.getDescription());
            item.setCategoryName(product.getCategoryName());
            cart.getItems().add(item);
        }

        cart.calculateTotalPrice();
        return orderDataRepository.save(cart);
    }

    // Remove an item from the cart and restore quantityStock
    @Override
    public Order removeItemFromCart(int customerId, int productId) {
        Order cart = getCartByCustomerId(customerId);
        if (cart != null && cart.getItems() != null) {
            Optional<OrderItems> itemOpt = cart.getItems().stream()
                    .filter(item -> item.getProductId() == productId)
                    .findFirst();

            if (itemOpt.isPresent()) {
                OrderItems item = itemOpt.get();

                // Restore stock and save
                Optional<ProductData> productOpt = productDataRepository.findById(productId);
                if (productOpt.isPresent()) {
                    ProductData product = productOpt.get();
                    product.setQuantityStock(product.getQuantityStock() + (int) item.getQuantity());
                    productDataRepository.save(product);
                }

                cart.getItems().remove(item);
                cart.calculateTotalPrice();
                return orderDataRepository.save(cart);
            }
        }
        return null;
    }

    @Override
    public Order updateItemQuantity(int customerId, int productId, double quantity) {
        Order cart = getCartByCustomerId(customerId);

        if (cart != null && cart.getItems() != null) {
            for (OrderItems item : cart.getItems()) {
                if (item.getProductId() == productId) {
                    log.info("Updating quantity for product {} to {}", productId, quantity);

                    // Update stock accordingly
                    Optional<ProductData> productOpt = productDataRepository.findById(productId);
                    if (productOpt.isPresent()) {
                        ProductData product = productOpt.get();
                        int stockDifference = (int) (quantity - item.getQuantity());
                        if (product.getQuantityStock() >= stockDifference) {
                            product.setQuantityStock(product.getQuantityStock() - stockDifference);
                            productDataRepository.save(product);
                            item.setQuantity(quantity);
                            cart.calculateTotalPrice();
                            return orderDataRepository.save(cart);
                        } else {
                            log.warn("Insufficient stock for product ID {}.", productId);
                        }
                    }
                }
            }
        }

        log.warn("Cart or item not found for customer {}", customerId);
        return null;
    }

    @Override
    public Order getCartByCustomerId(int customerId) {
        Optional<Order> optionalCart = orderDataRepository.findByCustomerIdAndStatus(customerId, "active");
        return optionalCart.orElse(null);
    }

    @Override
    public void clearCart(int customerId) {
        Order cart = getCartByCustomerId(customerId);
        if (cart != null) {
            // Restore stock for each item in the cart
            for (OrderItems item : cart.getItems()) {
                Optional<ProductData> productOpt = productDataRepository.findById(item.getProductId());
                if (productOpt.isPresent()) {
                    ProductData product = productOpt.get();
                    product.setQuantityStock(product.getQuantityStock() + (int) item.getQuantity());
                    productDataRepository.save(product);
                }
            }

            cart.getItems().clear();
            cart.calculateTotalPrice();
            orderDataRepository.save(cart);
        }
    }

    public Order generateAndSaveInvoice(Order order) {
        Order cart = getCartByCustomerId(order.getCustomerId());

        if (cart != null && "active".equals(cart.getStatus())) {
            cart.setPay(order.getPay());
            cart.setInvoice("Invoice generated."); // Simplified invoice message
            cart.setShip(order.getShip());
            cart.setPick(order.getPick());
            cart.setComplete(order.getComplete());
            cart.setCancel(order.getCancel());
            cart.setStatus("purchased");

            // Create a summary of the invoice with only customer ID and product names
            StringBuilder invoiceDetails = new StringBuilder();
            invoiceDetails.append("Customer ID: ").append(cart.getCustomerId()).append("\n")
                    .append("Products:\n");

            for (OrderItems item : cart.getItems()) {
                invoiceDetails.append("Product: ").append(item.getProductName()).append("\n");
            }

            cart.setInvoice(invoiceDetails.toString());

            return orderDataRepository.save(cart);
        }
        return null;
    }


    @Override
    public Order checkout(Order orderRequest) {
        Order existingOrder = getCartByCustomerId(orderRequest.getCustomerId());

        if (existingOrder != null && "active".equals(existingOrder.getStatus())) {
            existingOrder.setPay(orderRequest.getPay());
            existingOrder.setInvoice(orderRequest.getInvoice());
            existingOrder.setShip(orderRequest.getShip());
            existingOrder.setPick(orderRequest.getPick());
            existingOrder.setComplete(orderRequest.getComplete());
            existingOrder.setCancel(orderRequest.getCancel());
            existingOrder.setStatus("purchased");

            return orderDataRepository.save(existingOrder);
        }
        return null;
    }
}