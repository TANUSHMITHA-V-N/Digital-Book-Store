package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Order;

public interface OrderService {
    String saveOrder(Order order);
    Order updateOrder(Order order);
    Order getOrderById(Long orderid); 
    String deleteOrderById(Long orderid); 
    List<Order> getAllOrders();
    Order patchUpdateOrder(Long orderid, Order updatedFields); 
    List<Order> getOrdersByUserEmail(String userEmail);
}
