package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate; // Import LocalDate

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Book;
import com.example.demo.model.User; // Import User model
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.UserRepository; // Autowire UserRepository
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.UserNotFoundException; // For when user is not found

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository repository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public String saveOrder(Order order) {
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            logger.warn("Attempted to save an invalid order or an order without items.");
            throw new IllegalArgumentException("Invalid order details or no items in order.");
        }

        if (order.getUser() == null || order.getUser().getEmail() == null) {
            throw new IllegalArgumentException("User email is required for order creation.");
        }
        User user = userRepository.findByEmail(order.getUser().getEmail())
                         .orElseThrow(() -> new UserNotFoundException("User not found with email: " + order.getUser().getEmail()));
        order.setUser(user); 

        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }

        double calculatedTotalAmount = 0.0;

        for (OrderItem item : order.getOrderItems()) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> {
                        logger.error("Book not found for order item with ID: {}", item.getBookId());
                        return new BookNotFoundException("Book not found for order item with ID: " + item.getBookId());
                    });

            if (book.getStockquantity() < item.getQuantity()) {
                logger.error("Insufficient stock for book: {}. Available: {}, Requested: {}", book.getTitle(), book.getStockquantity(), item.getQuantity());
                throw new InsufficientStockException("Insufficient stock for book: " + book.getTitle() + ". Available: " + book.getStockquantity() + ", Requested: " + item.getQuantity());
            }

            book.setStockquantity(book.getStockquantity() - item.getQuantity());
            bookRepository.save(book);
            item.setBook(book); 
            item.setOrder(order); 

            if (item.getPriceAtOrder() == 0.0) { 
                item.setPriceAtOrder(book.getPrice());
            }
            calculatedTotalAmount += item.getPriceAtOrder() * item.getQuantity();
        }
        order.setTotalAmount(calculatedTotalAmount);

        repository.save(order);
        logger.info("Order saved successfully with ID: {}", order.getOrderid());
        return "Order saved successfully!";
    }

    @Override
    public Order updateOrder(Order order) {
        return repository.findById(order.getOrderid())
                .map(existingOrder -> {
                    if (order.getUser() != null && order.getUser().getEmail() != null) {
                        User user = userRepository.findByEmail(order.getUser().getEmail())
                                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + order.getUser().getEmail()));
                        existingOrder.setUser(user);
                    }
                    
                    existingOrder.setOrderDate(order.getOrderDate() != null ? order.getOrderDate() : existingOrder.getOrderDate());
                    existingOrder.setTotalAmount(order.getTotalAmount() > 0 ? order.getTotalAmount() : existingOrder.getTotalAmount());
                    existingOrder.setStatus(order.getStatus() != null && !order.getStatus().isEmpty() ? order.getStatus() : existingOrder.getStatus());
                    
                    
                    logger.info("Order updated successfully with ID: {}", order.getOrderid());
                    return repository.save(existingOrder);
                }).orElseThrow(() -> {
                    logger.error("Order not found for update with ID: {}", order.getOrderid());
                    return new OrderNotFoundException("Order not found with ID: " + order.getOrderid());
                });
    }


    @Override
    public Order getOrderById(Long orderid) {
        return repository.findById(orderid)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", orderid);
                    return new OrderNotFoundException("Order not found with ID: " + orderid);
                });
    }

    @Override
    @Transactional
    public String deleteOrderById(Long orderid) {
        Optional<Order> optionalOrder = repository.findById(orderid);
        if (!optionalOrder.isPresent()) {
            logger.warn("Attempted to delete non-existent order with ID: {}", orderid);
            throw new OrderNotFoundException("Order not found for deletion with ID: " + orderid);
        }

        Order orderToDelete = optionalOrder.get();

        if (orderToDelete.getOrderItems() != null) {
            for (OrderItem item : orderToDelete.getOrderItems()) {
                Book book = item.getBook();
                if (book != null) {
                    book.setStockquantity(book.getStockquantity() + item.getQuantity());
                    bookRepository.save(book);
                    logger.info("Replenished stock for book ID: {} by quantity: {}", book.getBookid(), item.getQuantity());
                } else {
                    logger.warn("Book reference is null for OrderItem ID: {} in order ID: {}", item.getId(), orderToDelete.getOrderid());
                }
            }
        } else {
            logger.warn("Order items list is null for order ID: {}. Cannot replenish stock.", orderToDelete.getOrderid());
        }

        repository.delete(orderToDelete);
        logger.info("Order deleted successfully with ID: {}", orderid);
        return "Order deleted successfully!";
    }
    
    

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = repository.findAll();
        if (orders.isEmpty()) {
             logger.info("No orders found in the system. Returning empty list.");
        }
        return orders;
    }

    @Override
    public Order patchUpdateOrder(Long orderid, Order updatedFields) {
        return repository.findById(orderid).map(existingOrder -> {
            if (updatedFields.getUser() != null && updatedFields.getUser().getEmail() != null) {
                User user = userRepository.findByEmail(updatedFields.getUser().getEmail())
                        .orElseThrow(() -> new UserNotFoundException("User not found with email: " + updatedFields.getUser().getEmail()));
                existingOrder.setUser(user);
            }
            if (updatedFields.getTotalAmount() > 0) { 
                existingOrder.setTotalAmount(updatedFields.getTotalAmount());
            }
            if (updatedFields.getStatus() != null && !updatedFields.getStatus().isEmpty()) {
                existingOrder.setStatus(updatedFields.getStatus());
            }
            if (updatedFields.getOrderDate() != null) {
                existingOrder.setOrderDate(updatedFields.getOrderDate());
            }

            logger.info("Order partially updated with ID: {}", orderid);
            return repository.save(existingOrder);
        }).orElseThrow(() -> {
            logger.warn("Order not found for patch update with ID: {}", orderid);
            return new OrderNotFoundException("Order not found with ID: " + orderid);
        });
        
        
    }

	@Override
	public List<Order> getOrdersByUserEmail(String userEmail) {
		User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

			List<Order> orders = repository.findByUser_Id(user.getId());
			orders.forEach(order -> {
			   if (order.getOrderItems() != null) {
			       order.getOrderItems().forEach(item -> {
			           if (item.getBook() != null) {
			               item.getBook().getTitle();
			           }
			       });
			   }
			});
			
			if (orders.isEmpty()) {
			   logger.info("No orders found for user with email: {}", userEmail);
			}
			return orders;
	}
}
