package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient; // Crucial import for @Transient

import com.fasterxml.jackson.annotation.JsonBackReference; // Import JsonBackReference

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private double priceAtOrder; 

    @ManyToOne
    @JoinColumn(name = "book_id") 
    private Book book;
    @Transient 
    private Long bookId;
        @ManyToOne
    @JoinColumn(name = "order_id") 
    @JsonBackReference
    private Order order;

    public OrderItem() {
    }

    
    public OrderItem(Long id, int quantity, double priceAtOrder, Book book, Order order) {
        this.id = id;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.book = book;
        this.order = order;
    }
    
    
    public OrderItem(int quantity, double priceAtOrder, Long bookId) {
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.bookId = bookId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(double priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    // Getter for the transient bookId field (used by OrderServiceImpl to fetch Book)
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
               "id=" + id +
               ", quantity=" + quantity +
               ", priceAtOrder=" + priceAtOrder +
               ", bookId (transient)=" + bookId + 
               ", book=" + (book != null ? book.getBookid() : "null") + 
               ", orderId=" + (order != null ? order.getOrderid() : "null") + 
               '}';
    }
}
