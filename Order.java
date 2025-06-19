package com.example.demo.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Import JsonManagedReference

@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderid;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(name = "orderDate", nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false, length = 20)
    private String status;

    @Version
    private int version;

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval = true) 
    @JsonManagedReference // This side will be serialized
    private List<OrderItem> orderItems;

    public Order() {}

    public Order(User user, LocalDate orderDate, double totalAmount, String status) {
        this.user = user;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "Order [orderid=" + orderid + ", user=" + (user != null ? user.getEmail() : "null") + // Avoid full user serialization
                ", orderDate=" + orderDate + ", totalAmount=" + totalAmount + ", status=" + status + 
                ", version=" + version + ", orderItemsCount=" + (orderItems != null ? orderItems.size() : 0) + "]";
    }
}
