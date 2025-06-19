package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService service;

    @PostMapping
    public ResponseEntity<String> saveOrder(@RequestBody Order order) {
        logger.info("Received request to save order.");
        try {
            String result = service.saveOrder(order);
            logger.info("Order saved successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (InsufficientStockException e) {
            logger.error("Insufficient stock for order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saving order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving order: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateOrder(@RequestBody Order order) {
        logger.info("Received request to update order ID: {}", order.getOrderid());
        try {
            Order updated = service.updateOrder(order);
            logger.info("Order updated successfully.");
            return ResponseEntity.ok(updated);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found for update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order: " + e.getMessage());
        }
    }

    @PatchMapping("/partial-update/{id}")
    public ResponseEntity<?> patchUpdateOrder(@PathVariable("id") Long orderId, @RequestBody Order updatedFields) {
        logger.info("Received request to patch update order ID: {}", orderId);
        try {
            Order patched = service.patchUpdateOrder(orderId, updatedFields);
            logger.info("Order patch update successful.");
            return ResponseEntity.ok(patched);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found for partial update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error patch updating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating order: " + e.getMessage());
        }
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") Long orderId) {
        logger.info("Fetching order by ID: {}", orderId);
        try {
            Order found = service.getOrderById(orderId);
            logger.info("Order retrieved successfully.");
            return ResponseEntity.ok(found);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found for fetch: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching order: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrderById(@PathVariable("id") Long orderId) {
        logger.info("Attempting to delete order ID: {}", orderId);
        try {
            String result = service.deleteOrderById(orderId);
            logger.info("Order deleted successfully.");
            return ResponseEntity.ok(result);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found for deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting order: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userEmail}")
    public ResponseEntity<?> getOrdersByUserEmail(@PathVariable String userEmail) {
        logger.info("Fetching orders for user email: {}", userEmail);
        try {
            List<Order> userOrders = service.getOrdersByUserEmail(userEmail);
            if (userOrders.isEmpty()) {
                logger.info("No orders found for user: {}", userEmail);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            logger.info("Orders found for user: {}", userEmail);
            return ResponseEntity.ok(userOrders);
        } catch (UserNotFoundException e) {
            logger.warn("User not found for order retrieval: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching orders for user {}: {}", userEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user orders: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        logger.info("Fetching all orders.");
        try {
            List<Order> allOrders = service.getAllOrders();
            logger.info("Retrieved {} orders.", allOrders.size());
            return ResponseEntity.ok(allOrders);
        } catch (OrderNotFoundException e) {
            logger.info("No orders found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (Exception e) {
            logger.error("Error fetching orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
